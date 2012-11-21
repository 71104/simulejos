#version 120

uniform float Focus = 2;
uniform vec3 RobotPosition;
uniform mat3 InverseRobotHeading;
uniform vec3 SensorPosition;
uniform mat3 InverseSensorHeading;

attribute vec4 in_Vertex;
varying vec4 ex_Vertex;

void main() {
	gl_Position = mat4(
		Focus, 0, 0, 0,
		0, Focus, 0, 0,
		0, 0, 0, 1,
		0, 0, 1, Focus * 2 - 1
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
	) * in_Vertex;
	ex_Vertex = mat4(
		0.5, 0, 0, 0,
		0, 0, 0.5, 0,
		0, 1, 0, 0,
		0, 0, 0, 1
	) * in_Vertex;
}
