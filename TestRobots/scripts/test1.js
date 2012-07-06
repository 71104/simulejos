var wheelSpan = 2;
var wheelDiameter = 1;

function tick(daa, dab, dac) {
	var dsa = daa * wheelDiameter * Math.PI;
	var dsb = dab * wheelDiameter * Math.PI;
	if (dsa > dsb) {
		var radius = dsa * wheelSpan / (dsa - dsb);
		var angle = dsa / radius;
		robot.moveBy(-Math.cos(angle), 0, Math.sin(angle));
		robot.rotateBy(0, 1, 0, -angle);
	} else if (dsb > dsa) {
		var radius = dsb * wheelSpan / (dsb - dsa);
		var angle = dsb / radius;
		robot.moveBy(Math.cos(angle), 0, Math.sin(angle));
		robot.rotateBy(0, 1, 0, angle);
	} else {
		robot.moveBy(0, 0, dsa);
	}
}
