package edu.jmaycon.playground.model;

import java.util.List;
import lombok.Builder;

@Builder
public record AccessRecords(
        List<AccessLog> accessLogs, CameraStatus cameraStatus, VehicleExit vehicleExit, String notes) {
    @Builder
    public record AccessLog(String person, String time) {}

    @Builder
    public record CameraStatus(String offlineFrom, String offlineTo) {}

    @Builder
    public record VehicleExit(String driver, String time, String vehicleId) {}
}
