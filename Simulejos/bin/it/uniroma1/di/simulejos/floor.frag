#version 120

uniform bool UseTexture;
uniform sampler2D Texture;

varying vec4 ex_Position;

void main() {
	if (UseTexture) {
		gl_FragColor = texture2DProj(Texture, ex_Position);
	} else {
		gl_FragColor = vec4(1);
	}
}
