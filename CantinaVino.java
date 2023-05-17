import java.util.Random; //cosi importo la libreria che genera numeri casuali

public class CantinaVino {
    private static final int numeroDeiBevitori = 10;
    private static final int tempoAttesaMassimo = 5000; 
    private static final int tempoBevutaMassimo = 2000;

    private static int vinoRimanente = 100;
    private static int rubinettiDisponibili = 3;

    private static final Object lock = new Object();

    private static class Bevitore extends Thread {
        private final int id;

        public Bevitore(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            Random rand = new Random();
            boolean staBevendo = true;

            while (staBevendo) {
                try {
                    Thread.sleep(rand.nextInt(tempoAttesaMassimo));

                    synchronized (lock) {   
                        if (rubinettiDisponibili == 0) {
                            System.out.println("Bevitore " + id + " è in attesa dei rubineti.");
                            lock.wait();
                        }

                        if (vinoRimanente > 0) {                           
                            System.out.println("Bevitore " + id + " sta bevendo.");
                            vinoRimanente--;
                            rubinettiDisponibili--;

                            
                            Thread.sleep(rand.nextInt(tempoBevutaMassimo));

                            rubinettiDisponibili++;
                            System.out.println("Bevitore " + id + " a finito di bere. Vino rimanente: " + vinoRimanente);

                            lock.notifyAll();
                        } else {    // cosi con u cout mostro chi è l'ultimo ad aver finito
                            System.out.println("Bevitore " + id + " ha finito tutto il vino.");
                            staBevendo = false;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // il main l'ho fatto qui per comodita mia
    public static void main(String[] args) {
        Thread[] bevitori = new Thread[numeroDeiBevitori];

        for (int i = 0; i < numeroDeiBevitori; i++) {
            bevitori[i] = new Bevitore(i + 1);
            bevitori[i].start();
        }

        for (int i = 0; i < numeroDeiBevitori; i++) {
            try {
                bevitori[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Tutti i bevitori hanno finito");
    }
}