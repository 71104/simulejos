#version 120

uniform Focus = 1;
uniform vec3 RobotPosition;
uniform mat3 InverseRobotHeading;
uniform vec3 SensorPosition;
uniform mat3 InverseSensorHeading;
uniform float Size;

attribute vec3 in_Vertex;

attribute vec4 in_Vertex;
varying vec4 ex_Position;

void main() {
	ex_Position = mat4(
		1, 0, 0, 0,
		0, 0, 1, 0,
		0, 1, 0, 0,
		0, 0, 0, 1
	) * in_Vertex;
	gl_Position = mat4(
		Focus, 0, 0, 0,
		0, Focus, 0, 0,
		0, 0, 0, 1,
		0, 0, 1, Focus
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
		0, -1, -Focus, 1
	) * vec4(in_Vertex, 1);
}
