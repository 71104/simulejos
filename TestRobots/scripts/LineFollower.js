importPackage(Packages.it.uniroma1.di.simulejos.math);

var wheelSpan = robot.boundingBox.size.x;
var wheelDiameter = robot.boundingBox.size.y;
var wheelRadius = wheelDiameter / 2;

robot.S1.lightSensor(
	new Vector3(
		robot.boundingBox.center.x,
		robot.boundingBox.min.y + 0.1,
		robot.boundingBox.max.z + 0.1
		),
	Matrix3.create([1, 0, 0, 0, 0, -1, 0, 1, 0])
	);

function tick(daa, dab, dac) {
	daa *= 4; // rapporto di trasmissione
	dab *= 4; // rapporto di trasmissione
	if (daa != dab) {
		var ra = daa * wheelSpan / (dab - daa);
		var radius = ra + wheelSpan / 2;
		var angle = (daa + dab) / (2 * radius);
		robot.moveBy(radius * (Math.cos(angle) - 1), 0, radius * Math.sin(angle));
		robot.rotateBy(0, 1, 0, angle);
	} else {
		robot.moveBy(0, 0, daa * wheelRadius);
	}
}
