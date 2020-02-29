package org.firstinspires.ftc.teamcode.TeleopPrograms;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotClasses.Logger;
import org.firstinspires.ftc.teamcode.RobotClasses.Robot;

import java.util.Arrays;

@TeleOp
public class Teleop extends LinearOpMode {

    private Robot robot;
    private boolean robotCentric = true;
    private boolean dpadUp = true;
    private boolean dpadDown = true;
    private boolean rightBumper = true;
    private boolean leftBumper = true;

    @Override
    public void runOpMode() {
        /*double[] initialPosition = Logger.readPos();
        telemetry.addData("Starting Position", Arrays.toString(initialPosition)); telemetry.update();
        robot = new Robot(this, initialPosition[0], initialPosition[1], initialPosition[2]);*/
        robot = new Robot(this, 0, 0, 0, false);
        robot.logger.startLogging();
        robot.stacker.unClampStone();
        robot.stacker.goHome();

        waitForStart();

        while (opModeIsActive()) {

            // move arm to deposit position
            if (gamepad2.right_bumper && rightBumper) {
                rightBumper = false;
            } else if (!rightBumper && !gamepad2.right_bumper) {
                robot.deposit();
                rightBumper = true;
            }

            // let go of the stone
            if (gamepad2.left_bumper && leftBumper) {
                leftBumper = false;
            } else if (!leftBumper && !gamepad2.left_bumper) {
                robot.letGo = true;
                leftBumper = true;
            }

            // reverse the intake
            if (gamepad1.b) {
                robot.intake.setControls(-1);
                robot.intakeManual = true;
            } else {robot.intakeManual = false;}

            // grab/release foundation
            if (gamepad1.dpad_left) {robot.grabber.grabFoundation();}
            if (gamepad1.dpad_right) {robot.grabber.releaseFoundation();}

            // capstone deposit
            if (gamepad2.a) {robot.capstoneDeposit.attachCapstone();}
            else {robot.capstoneDeposit.goHome();}

            if(gamepad2.x){
                robot.cheesemode = true;
            }

            // increase stack level
            if (gamepad2.dpad_up && dpadUp) {
                dpadUp = false;
            } else if (!dpadUp && !gamepad2.dpad_up) {
                robot.stacker.nextLevel();
                dpadUp = true;
            }

            // decrease stack level
            if (gamepad2.dpad_down && dpadDown) {
                dpadDown = false;
            } else if (!dpadDown && !gamepad2.dpad_down) {
                robot.stacker.lastLevel();
                dpadDown = true;
            }

            // stacker/lift manual controls
            if(gamepad2.right_trigger>0){
                robot.stacker.manualArmOffset += 2;
            }
            else if(gamepad2.left_trigger>0){
                robot.stacker.manualArmOffset -= 2;
            }

            // robot centric
            if (robotCentric /*&& (gamepad1.left_stick_x!=0 || gamepad1.right_stick_x!=0 || gamepad1.left_stick_y !=0)*/) {
                robot.drivetrain.setControls(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
                //robot.isManualAlign = true;
            }
            // field centric
            else {
                robot.drivetrain.setGlobalControls(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
            }
            /*else{
                robot.isManualAlign = false;
                robot.drivetrain.setControls(0,0,0);
            }*/

            robot.update();
            telemetry.addData("arm ticks", robot.stacker.getArmPosition());
            telemetry.addData("slide ticks", robot.stacker.getLiftPosition());
            telemetry.addData("stack height", robot.stacker.currentStackHeight);
            telemetry.update();
        }

        robot.logger.stopLogging();
    }
}
