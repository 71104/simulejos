#version 120

uniform vec3 Color = vec3(1, 0, 0);

varying vec4 ex_Vertex;

void main() {
	gl_FragColor = vec4(Color * ex_Vertex.z / ex_Vertex.w, 1);
}
