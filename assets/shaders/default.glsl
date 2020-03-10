#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;

out vec3 oPos;
out vec4 oColor;

void main()
{
    oPos = aPos;
    oColor = aColor;
    gl_Position = vec4(aPos, 1.0);
}

#type fragment
#version 330 core
out vec4 color;

in vec3 oPos;
in vec4 oColor;

void main()
{
    color = oColor;
}