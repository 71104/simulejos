#version 120

const mat4 ModelViewProjection = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
);

attribute vec3 in_Vertex;

void main() {
	gl_Position = ModelViewProjection * vec4(in_Vertex, 1);
}
