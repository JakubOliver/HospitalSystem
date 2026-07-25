package cz.cuni.kubinja.hospitalsystem.TUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics;

import java.util.Locale;
import java.util.Scanner;

/**
 * Menu page containing options connected with data reports.
 */
public class ReportsMenu extends Menu{
    /**
     * Creates report menu page.
     *
     * @param api Hospital api diving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public ReportsMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Show statistics", this::statistics);
        addOption("Back", this::end);
    }

    /**
     * Computes and prints statistics about hospital, patients, doctors and appointments.
     */
    public void statistics(){
        DataPacket<HospitalStatistics> packet = api.getStatistics();
        if (!processPacketStatusInSilence(packet)) return;
        HospitalStatistics statistics = packet.data;

        System.out.println("Absolute values");
        System.out.println("\tPatients: " + statistics.patientCount());
        System.out.println("\tDoctors: " + statistics.doctorCount());
        System.out.println("\tAppointments: " + statistics.appointmentCount());

        System.out.println("Averages");
        System.out.println(
                "\tIn average every patient was on "
                        + String.format(
                                Locale.ROOT,
                                "%.3f",
                                statistics.averageAppointmentsPerPatient()
                        )
                        + " appointments"
        );
        System.out.println(
                "\tIn average every doctor worked "
                        + String.format(
                                Locale.ROOT,
                                "%.3f",
                                statistics.averageAppointmentsPerDoctor()
                        )
                        + " appointments"
        );

        System.out.println("5 patients with most visits:");
        for (int i = 0; i < statistics.patientsWithMostVisits().size(); ++i){
            HospitalStatistics.PatientVisits patient =
                    statistics.patientsWithMostVisits().get(i);
            System.out.println(
                    "\t" + (i + 1) + ". "
                            + patient.firstName() + " " + patient.lastName()
                            + " with " + patient.appointmentCount() + " appointments"
            );
        }

        System.out.println("10 most common anamnesis:");
        for (int i = 0; i < statistics.commonAnamneses().size(); ++i){
            HospitalStatistics.Occurrence anamnesis = statistics.commonAnamneses().get(i);
            System.out.println(
                    "\t" + (i + 1) + ". " + anamnesis.value() + ":  " + anamnesis.count()
            );
        }

        System.out.println("10 most common specializations:");
        for (int i = 0; i < statistics.commonSpecializations().size(); ++i){
            HospitalStatistics.Occurrence specialization =
                    statistics.commonSpecializations().get(i);
            System.out.println(
                    "\t" + (i + 1) + ". "
                            + specialization.value() + ":  " + specialization.count()
            );
        }

        waitForEnter();
    }
}
