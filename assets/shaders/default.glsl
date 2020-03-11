#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aScale;
layout (location = 2) in vec4 aColor;
layout (location = 3) in vec2 aTexCoords;

out vec3 oPos;
out vec4 oColor;

uniform mat4 uView;
uniform mat4 uProjection;

void main()
{
    oPos = aPos;
    oColor = aColor;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
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