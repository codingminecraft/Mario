#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in vec4 borderRadius;
layout (location = 4) in vec4 borderColor;
layout (location = 5) in float borderWidth;
layout (location = 6) in vec2 dimensions;
layout (location = 7) in float texSlot;

out vec3 fPos;
out vec4 fColor;
out vec2 fTexCoords;
out vec4 fBorderRadius;
out vec4 fBorderColor;
out float fBorderWidth;
out vec2 fDimensions;
out float fTexSlot;

uniform mat4 uView;
uniform mat4 uProjection;

void main()
{
    fPos = aPos;
    fColor = aColor;
    fTexCoords = aTexCoords;
    fBorderRadius = borderRadius;
    fBorderColor = borderColor;
    fBorderWidth = borderWidth;
    fDimensions = dimensions;
    fTexSlot = texSlot;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
uniform float uAspect;

out vec4 color;

in vec3 fPos;
in vec4 fColor;
in vec2 fTexCoords;
in vec4 fBorderRadius;
in vec4 fBorderColor;
in float fBorderWidth;
in vec2 fDimensions;
in float fTexSlot;

uniform sampler2D TEX_1;
uniform sampler2D TEX_2;
uniform sampler2D TEX_3;
uniform sampler2D TEX_4;
uniform sampler2D TEX_5;
uniform sampler2D TEX_6;
uniform sampler2D TEX_7;

void isBorder(in vec2 position, in vec2 cornerPosition, in float outerRadius, in float borderWidth, in vec4 borderColor, in vec4 fillColor, out vec4 color)
{
    if (outerRadius == 0) {
        color = fBorderColor;
        return;
    }

    float innerRadius = outerRadius - borderWidth;
    if (innerRadius < 0) {
        innerRadius = 0;
        outerRadius = borderWidth;
    }

    if (length(position - cornerPosition) < outerRadius && length(position - cornerPosition) > innerRadius) {
        color = borderColor;
    } else if (length(position - cornerPosition) < innerRadius) {
        color = fillColor;
    } else {
        color = vec4(0, 0, 0, 0);
    }
}

void main()
{
    vec4 texColor = vec4(1, 1, 1, 1);
    if (fTexSlot > 0) {
        if (fTexSlot < 2) {
            texColor = texture(TEX_1, fTexCoords);
        } else if (fTexSlot < 3) {
            texColor = texture(TEX_2, fTexCoords);
        } else if (fTexSlot < 4) {
            texColor = texture(TEX_3, fTexCoords);
        } else if (fTexSlot < 5) {
            texColor = texture(TEX_4, fTexCoords);
        } else if (fTexSlot < 6) {
            texColor = texture(TEX_5, fTexCoords);
        } else if (fTexSlot < 7) {
            texColor = texture(TEX_6, fTexCoords);
        } else if (fTexSlot < 8) {
            texColor = texture(TEX_7, fTexCoords);
        }
    }

    if (fBorderWidth > 0) {
        float maxX = fDimensions.x - fBorderWidth;
        float minX = fBorderWidth;
        float maxY = (maxX / uAspect);
        float minY = (minX / uAspect);
        vec2 pos = fTexCoords * fDimensions;

        vec2 topLeft = vec2(max(minX, fBorderRadius.x), min(maxY, fDimensions.y - fBorderRadius.x));
        vec2 bottomLeft = vec2(max(minX, fBorderRadius.z), max(minY, fBorderRadius.z));
        vec2 topRight = vec2(min(maxX, fDimensions.x - fBorderRadius.y), min(maxY, fDimensions.y - fBorderRadius.y));
        vec2 bottomRight = vec2(min(maxX, fDimensions.x - fBorderRadius.w), max(minY, fBorderRadius.w));

        if (pos.y > bottomLeft.y && pos.y < topLeft.y && pos.x < topRight.x && pos.x < bottomRight.x) {
            if (pos.x < minX) {
                color = fBorderColor;
            } else {
                color = fColor;
            }
        } else if (pos.y > bottomRight.y && pos.y < topRight.y && pos.x > topLeft.x && pos.x > bottomLeft.x) {
            if (pos.x > maxX) {
                color = fBorderColor;
            }   else {
                color = fColor;
            }
        } else if (pos.x > bottomLeft.x && pos.x < bottomRight.x && pos.y < bottomLeft.y && pos.y < bottomRight.y) {
            if (pos.y < minY) {
                color = fBorderColor;
            } else {
                color = fColor;
            }
        } else if (pos.x > topLeft.x && pos.x < topRight.x && pos.y > topLeft.y && pos.y > topRight.y) {
            if (pos.y > maxY) {
                color = fBorderColor;
            } else {
                color = fColor;
            }
        } else if (pos.y > topLeft.y && pos.x < topLeft.x) {
            isBorder(pos, topLeft, fBorderRadius.x, fBorderWidth, fBorderColor, fColor, color);
        } else if (pos.y < bottomLeft.y && pos.x < bottomLeft.x) {
            isBorder(pos, bottomLeft, fBorderRadius.z, fBorderWidth, fBorderColor, fColor, color);
        } else if (pos.y > topRight.y && pos.x > topRight.x) {
            isBorder(pos, topRight, fBorderRadius.y, fBorderWidth, fBorderColor, fColor, color);
        } else if (pos.y < bottomRight.y && pos.x > bottomRight.x) {
            isBorder(pos, bottomRight, fBorderRadius.w, fBorderWidth, fBorderColor, fColor, color);
        }
    } else if (fColor.w > 0.0 && fTexSlot < 1) {
        color = fColor;
    } else if (fTexSlot > 0) {
        color = texColor * fColor;
    } else {
        color = vec4(0, 0, 0, 0);
    }
}