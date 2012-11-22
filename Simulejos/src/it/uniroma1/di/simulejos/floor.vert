#version 120

uniform struct {
	vec2 Angle;
	vec3 Position;
} Camera;

mat4 ModelViewProjection = mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 0, 1,
	0, 0, 1, 0
) * mat4(
	1, 0, 0, 0,
	0, cos(Camera.Angle.y), sin(Camera.Angle.y), 0,
	0, -sin(Camera.Angle.y), cos(Camera.Angle.y), 0,
	0, 0, 0, 1
) * mat4(
	cos(Camera.Angle.x), 0, -sin(Camera.Angle.x), 0,
	0, 1, 0, 0,
	sin(Camera.Angle.x), 0, cos(Camera.Angle.x), 0,
	0, 0, 0, 1
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	-Camera.Position, 1
) * mat4(
	1, 0, 0, 0,
	0, 1, 0, 0,
	0, 0, 1, 0,
	0, -1, 0, 1
);

attribute vec4 in_Vertex;
varying vec4 ex_Vertex;

void main() {
	gl_Position = ModelViewProjection * in_Vertex;
	ex_Vertex = mat4(
		0.5, 0, 0, 0,
		0, 0, 1, 0,
		0, -0.5, 0, 0,
		0, 0, 0, 1
	) * in_Vertex;
}
