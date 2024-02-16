package ru.netology.patient.service.medical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import static org.mockito.Mockito.*;

import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

class MedicalServiceImplTest {
    static final String id = "12301hdqq31ef";
    @ParameterizedTest
    @ValueSource(strings = {
            "38.2",
            "40.0"
    })
    void checkBadTemperatureTest(String currentTemp) {
        PatientInfoFileRepository patientInfo = mock(PatientInfoFileRepository.class);
        SendAlertService alertService = mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfo, alertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        when(patientInfo.getById(id))
                .thenReturn(new PatientInfo(id, "name", "surname", LocalDate.of(1999, 6, 1),
                        new HealthInfo(new BigDecimal(currentTemp), new BloodPressure(120, 70))));

        medicalService.checkTemperature(id, new BigDecimal(36.6));

        verify(alertService, only()).send(argumentCaptor.capture());

        String message = String.format("Warning, patient with id: %s, need help", id);
        assertEquals(message, argumentCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "36.6",
            "37.0",
            "38.1",
    })
    void checkGoodTemperatureTest(String currentTemp) {
        PatientInfoFileRepository patientInfo = mock(PatientInfoFileRepository.class);
        SendAlertService alertService = mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfo, alertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        when(patientInfo.getById(id))
                .thenReturn(new PatientInfo(id, "name", "surname", LocalDate.of(1999, 6, 1),
                        new HealthInfo (new BigDecimal(currentTemp), new BloodPressure(120, 70))));

        medicalService.checkTemperature(id, new BigDecimal(36.6));

        verify(alertService, never()).send(argumentCaptor.capture());
    }


    @ParameterizedTest
    @CsvSource(value = {
            "100, 60",
            "140, 80",
    })
    void checkBadBloodPressure(int hight, int low) {
        PatientInfoFileRepository patientInfo = mock(PatientInfoFileRepository.class);
        SendAlertService alertService = mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfo, alertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        when(patientInfo.getById(id))
                .thenReturn(new PatientInfo(id, "name", "surname", LocalDate.of(1999, 6, 1),
                        new HealthInfo (new BigDecimal(36.6), new BloodPressure(hight, low))));

        medicalService.checkBloodPressure(id, new BloodPressure(120, 75));
        verify(alertService, only()).send(argumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "120, 75",
    })
    void checkGoodBloodPressure(int hight, int low) {
        PatientInfoFileRepository patientInfo = mock(PatientInfoFileRepository.class);
        SendAlertService alertService = mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfo, alertService);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        when(patientInfo.getById(id))
                .thenReturn(new PatientInfo(id, "name", "surname", LocalDate.of(1999, 6, 1),
                        new HealthInfo (new BigDecimal(36.6), new BloodPressure(hight, low))));

        medicalService.checkBloodPressure(id, new BloodPressure(120, 75));
        verify(alertService, never()).send(argumentCaptor.capture());
    }
}