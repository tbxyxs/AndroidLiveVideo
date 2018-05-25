precision mediump float;
uniform vec4 uColor;
uniform sampler2D uTextureUnit;
varying vec2 vTexture;
void main()
{
    gl_FragColor = texture2D(uTextureUnit,vTexture);
}