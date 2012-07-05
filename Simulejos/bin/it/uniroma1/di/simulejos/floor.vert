#version 120

uniform struct {
	vec3 Position;
	mat3 Heading;
} Camera;

mat4 ViewProjection = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
) * mat4(
	Camera.Heading[0][0], Camera.Heading[1][0], Camera.Heading[2][0], 0,
	Camera.Heading[0][1], Camera.Heading[1][1], Camera.Heading[2][1], 0,
	Camera.Heading[0][2], Camera.Heading[1][2], Camera.Heading[2][2], 0,
	0, 0, 0, 1
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	-Camera.Position, 1
);

mat4 Model = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	0, -1, 0, 1
);

attribute vec4 in_Vertex;
varying vec4 ex_Position;

void main() {
	ex_Position = Model * in_Vertex;
	gl_Position = ViewProjection * ex_Position;
}
