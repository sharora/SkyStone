package org.firstinspires.ftc.teamcode.RobotClasses;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@SuppressWarnings("FieldCanBeLocal")
//@Config
public class FoundationGrabber {

    //Electronics
    private Servo leftGrabber;
    private Servo rightGrabber;

    public static double Lhome = 0.65;
    public static double Rhome = 0.37;
    public static double Lclamped = 0.27;
    public static double Rclamped = 0.8;


    //OpMode Stuff
    private LinearOpMode op;
    private HardwareMap hardwareMap;

    public FoundationGrabber(LinearOpMode op){

        this.op = op;
        this.hardwareMap = op.hardwareMap;

        leftGrabber = hardwareMap.get(Servo.class, "leftGrabber");
        rightGrabber = hardwareMap.get(Servo.class, "rightGrabber");
        op.telemetry.addData("Status", "Grabbers Initialized");
        op.telemetry.update();
    }

    public void releaseFoundation() {
        leftGrabber.setPosition(Lhome);
        rightGrabber.setPosition(Rhome);
    }
    public void grabFoundation() {
        leftGrabber.setPosition(Lclamped);
        rightGrabber.setPosition(Rclamped);
    }

    /*public void extendRangeSensor(){
        rightGrabber.setPosition(0.75);
    }
    public double getDistance(){
        return autoAlignSensor.getDistance(DistanceUnit.INCH);
    }

    public double getGrabberPosition() {
        return rightGrabber.getPosition();
    }*/
}
