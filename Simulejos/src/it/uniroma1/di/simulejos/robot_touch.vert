#version 120

uniform float Size;
uniform vec3 RobotPosition;
uniform mat3 InverseRobotHeading;
uniform vec3 SensorPosition;
uniform mat3 InverseSensorHeading;
uniform vec3 TargetRobotPosition;
uniform mat3 TargetRobotHeading;

attribute vec3 in_Vertex;

void main() {
	ex_Vertex = mat4(
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
		TargetRobotPosition, 1
	) * mat4(TargetRobotHeading) * vec4(in_Vertex, 1);
	gl_Position = ex_Vertex;
}
