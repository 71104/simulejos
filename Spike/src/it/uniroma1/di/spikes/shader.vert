#version 120

const mat4 Matrix = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	0, 0, 3, 1
) * mat4(
	1, 0, -1, 0,
	0, 1, 0, 0,
	1, 0, 1, 0,
	0, 0, 0, 1
);

attribute vec4 in_Vertex;

void main() {
	gl_Position = Matrix * in_Vertex;
}
