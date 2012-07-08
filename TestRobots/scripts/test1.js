var wheelSpan = 2;
var wheelDiameter = 1;

function tick(daa, dab, dac) {
	var dsa = daa * wheelDiameter * Math.PI;
	var dsb = dab * wheelDiameter * Math.PI;
	if (dsa * dsb < 0) {
		// TODO
	} else {
		if (dsa != dsb) {
			var angle = (dsa - dsb) / wheelSpan;
			var radius = wheelSpan * ((dsa + dsb) / (dsa - dsb)) / 2;
			robot.moveBy(radius - Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
			robot.rotateBy(0, 1, 0, -angle);
		} else {
			robot.moveBy(0, 0, dsa);
		}
	}
}
