package com.jade;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {
    private int bufferPointer;
    private int sourcePointer;
    private IntBuffer sourceState = stackMallocInt(1);

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops) {
        //Allocate space to store return information from the function
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);

        //Retreive the extra information that was stored in the buffers by the function
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        //Free the space we allocated earlier
        stackPop();
        stackPop();

        //Find the correct OpenAL format
        int format = -1;
        if(channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if(channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        //Request space for the buffer
        bufferPointer = alGenBuffers();

        //Send the data to OpenAL
        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

        // Generate the source pointer to play the sound
        sourcePointer = alGenSources();

        //Assign our buffer to the source
        alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
        alSourcei(sourcePointer, AL_LOOPING, loops ? 1 : 0);
        alSourcei(sourcePointer, AL_POSITION, 0);

        //Free the memory allocated by STB
        free(rawAudioBuffer);
    }

    public void delete() {
        alDeleteBuffers(bufferPointer);
    }

    public void play() {
        int state = alGetSourcei(sourcePointer, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
            alSourcei(sourcePointer, AL_POSITION, 0);
        }

        if (!isPlaying) {
            alSourcePlay(sourcePointer);
            isPlaying = true;
        }
    }

    public void stop() {
        if (isPlaying) {
            alSourceStop(sourcePointer);
            isPlaying = false;
        }
    }
}
