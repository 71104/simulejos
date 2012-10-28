#version 120

uniform vec3 RobotPosition;
uniform mat3 RobotHeading;
uniform vec3 SensorPosition;
uniform mat3 SensorHeading;
uniform float Size;

attribute vec3 in_Vertex;

void main() {
	gl_Position = mat4(
		1 / Size, 0, 0, 0,
		0, 1 / Size, 0, 0,
		0, 0, 1 / Size, 0,
		0, 0, 0, 1
	) * -mat4(SensorHeading) * mat4(
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		-SensorPosition, 1
	) * -mat4(RobotHeading) * mat4(
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		-RobotPosition, 1
	) * vec4(in_Vertex, 1);
}
