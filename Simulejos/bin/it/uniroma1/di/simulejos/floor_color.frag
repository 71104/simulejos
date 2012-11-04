#version 120

uniform vec3 Color = vec3(1, 0, 0);
uniform bool UseTexture;
uniform sampler2D Texture;

varying vec4 ex_Position;

void main() {
	if (UseTexture) {
		gl_FragColor = vec4(Color, 1) * texture2DProj(Texture, ex_Position);
	} else {
		gl_FragColor = vec4(Color * ex_Vertex.z / ex_Vertex.w, 1);
	}
}
