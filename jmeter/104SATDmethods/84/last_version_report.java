        public void run(){
            System.out.println("Reading responses from server ...");
            int x = 0;
            try {
                while ((x = is.read()) > -1) {
                    char c = (char) x;
                    System.out.print(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("... disconnected from server.");
            }
        }
