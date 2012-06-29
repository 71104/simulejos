#version 120

const mat4 ModelViewProjection = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	0, -1, 0, 1
);

attribute vec4 in_Vertex;
varying vec4 ex_Vertex;

void main() {
	ex_Vertex = ModelViewProjection * in_Vertex;
}
