#version 120

uniform bool UseTexture;
uniform sampler2D Texture;

varying vec4 ex_Vertex;

void main() {
	if (UseTexture) {
		gl_FragColor = texture2DProj(Texture, ex_Vertex);
	} else {
		gl_FragColor = vec4(1);
	}
}
