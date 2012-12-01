#version 120

uniform float Size;
uniform vec3 RobotPosition;
uniform mat3 InverseRobotHeading;
uniform vec3 SensorPosition;
uniform mat3 InverseSensorHeading;

attribute vec3 in_Vertex;

void main() {
	gl_Position = mat4(
		1 / Size, 0, 0, 0,
		0, 1 / Size, 0, 0,
		0, 0, 1 / Size, 0,
		0, 0, 0, 1
	) * mat4(InverseSensorHeading) * mat4(
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		-SensorPosition, 1
	) * mat4(InverseRobotHeading) * mat4(
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		-RobotPosition, 1
	) * mat4(
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		0, -1, 0, 1
	) vec4(in_Vertex, 1);
}
