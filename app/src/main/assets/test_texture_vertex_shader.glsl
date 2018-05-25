attribute vec4 aPosition;
uniform mat4 uMatrix;
attribute vec2 aTexture;
varying vec2 vTexture;
void main()
{
    vTexture=aTexture;
    gl_Position =uMatrix*aPosition;
}