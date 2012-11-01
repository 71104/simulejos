#version 120

varying vec4 ex_Position;

void main() {
	gl_FragColor = vec4(ex_Position.z / ex_Position.w);
}
