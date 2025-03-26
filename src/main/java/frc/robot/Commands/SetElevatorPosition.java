package frc.robot.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.controller.PIDController;
import frc.robot.Subsystems.*;

public class SetElevatorPosition extends Command {
    private ElevatorSubsystem elevator;
    private double position;
    private PIDController pid;
    private double goal;

    public SetElevatorPosition(ElevatorSubsystem elevator, double goal) {
        this.elevator = elevator;
        this.pid = new PIDController(0.1, 0.0, 0.0);
        this.position = elevator.getElevatorEncoder().getPosition();
        this.goal = goal;
        addRequirements(elevator);
    }

    @Override
    public void initialize() {
        pid.setSetpoint(goal);
    }

    @Override
    public void execute() {
        position = elevator.getCMRaised();
        double speed = pid.calculate(position);
        double deadzone = 0.2;
        elevator.setElevatorSpeed(speed, false);

        if (Math.abs(goal - elevator.getCMRaised()) < deadzone) {
            elevator.setElevatorSpeed(0, false);
            this.cancel();
        }
    }

    @Override
    public void end(boolean interrupted) {
        elevator.setElevatorSpeed(0, false);
    }
    
}
