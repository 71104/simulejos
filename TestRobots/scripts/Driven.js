importPackage(Packages.it.uniroma1.di.simulejos.math);

var wheelSpan = robot.boundingBox.size.x;
var wheelDiameter = robot.boundingBox.size.y;
var wheelRadius = wheelDiameter / 2;

robot.S1.compassSensor(Matrix3.create([1, 0, 0, 0, 1, 0, 0, 0, 1]));

function tick(daa, dab, dac) {
	daa *= 4; // rapporto di trasmissione
	dab *= 4; // rapporto di trasmissione
	if (daa != dab) {
		var radius = wheelSpan * (dab + daa) / (2 * (dab - daa));
		var angle = wheelRadius * (dab - daa) / wheelSpan;
		robot.moveBy(radius * (Math.cos(angle) - 1), 0, radius * Math.sin(angle));
		robot.rotateBy(0, 1, 0, angle);
	} else {
		robot.moveBy(0, 0, daa * wheelRadius);
	}
}
