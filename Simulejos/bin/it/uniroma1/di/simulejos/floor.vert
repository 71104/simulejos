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

void main() {
	gl_Position = ModelViewProjection * in_Vertex;
}
