#version 120

uniform float PassThrough;
varying vec4 ex_Vertex;

void main() {
	gl_FragColor = vec4((vec3(ex_Vertex / ex_Vertex.w) + 1) / 2, PassThrough);
}
