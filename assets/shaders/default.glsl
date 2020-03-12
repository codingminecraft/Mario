#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in vec4 borderRadius;
layout (location = 4) in vec4 borderColor;
layout (location = 5) in float borderWidth;
layout (location = 6) in vec2 dimensions;

out vec3 fPos;
out vec4 fColor;
out vec2 fTexCoords;
out vec4 fBorderRadius;
out vec4 fBorderColor;
out float fBorderWidth;
out vec2 fDimensions;

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
    } else {
        color = fColor;
    }
}