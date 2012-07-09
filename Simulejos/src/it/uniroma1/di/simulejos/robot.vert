#version 120

uniform struct {
	vec3 Position;
	mat3 Heading;
} Camera;

uniform vec3 Position;
uniform mat3 Heading;

mat4 ModelViewProjection = mat4(
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
) * mat4(
	Heading[0][0], Heading[1][0], Heading[2][0], 0,
	Heading[0][1], Heading[1][1], Heading[2][1], 0,
	Heading[0][2], Heading[1][2], Heading[2][2], 0,
	0, 0, 0, 1
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	Position, 1
);

attribute vec4 in_Vertex;

void main() {
	gl_Position = ModelViewProjection * in_Vertex;
}
