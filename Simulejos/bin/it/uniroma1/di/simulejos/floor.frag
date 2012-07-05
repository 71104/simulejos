#version 120

const float PI = acos(-1);
const vec3 Light = vec3(0, 2, 0);

varying vec4 ex_Position;

float Angle(vec3 a, vec3 b) {
	return acos(dot(a, b) / (length(a) * length(b)));
}

void main() {
	vec3 Position = vec3(ex_Position) / ex_Position.w;
	float Brightness = (PI - Angle(-Light, Position - Light)) / PI;
	gl_FragColor = vec4(1) * pow(Brightness, 2);
}
