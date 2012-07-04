#version 120

mat4 Matrix = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
);

attribute vec4 in_Vertex;

void main() {
	gl_Position = Matrix * in_Vertex;
}
