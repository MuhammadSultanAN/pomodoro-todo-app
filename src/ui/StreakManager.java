
package ui;

import java.io.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

public class StreakManager {

    private static final String FILE_NAME = "streak_data.dat";

    private Set<LocalDate> activeDays = new TreeSet<>();

    public StreakManager() {
        loadData();
    }

    public void recordSession() {
        LocalDate today = LocalDate.now();

        if (activeDays.add(today)) {
            saveData();
        }
    }

    public int getCurrentStreak() {
        if (activeDays.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastActive = activeDays.stream()
                .max(LocalDate::compareTo)
                .get();

        if (lastActive.isBefore(today.minusDays(1))) {
            return 0;
        }

        int streak = 0;
        LocalDate date = lastActive;

        while (activeDays.contains(date)) {
            streak++;
            date = date.minusDays(1);
        }

        return streak;
    }

    public int getLongestStreak() {
        int longest = 0;
        int current = 0;

        LocalDate previous = null;

        for (LocalDate date : activeDays) {

            if (previous != null &&
                    date.equals(previous.plusDays(1))) {
                current++;
            } else {
                current = 1;
            }

            longest = Math.max(longest, current);
            previous = date;
        }

        return longest;
    }

    public int getTotalActiveDays() {
        return activeDays.size();
    }

    public Set<LocalDate> getActiveDays() {
        return new TreeSet<>(activeDays);
    }

    private void saveData() {
        try (ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(FILE_NAME))) {

            out.writeObject(activeDays);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in =
                new ObjectInputStream(
                        new FileInputStream(file))) {

            activeDays = (Set<LocalDate>) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            activeDays = new TreeSet<>();
        }
    }
}
