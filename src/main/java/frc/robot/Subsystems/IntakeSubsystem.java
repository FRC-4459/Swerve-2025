package frc.robot.Subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    private SparkMax motor;

    public IntakeSubsystem() {
        motor = new SparkMax(15, MotorType.kBrushless);
    }

    public void run(double speed) {
        motor.set(speed);
    }
}
