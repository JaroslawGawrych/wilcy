import java.io.*;

public class Main {

	public static void main(String[] args) {

		Ssak[] tmp = Ssak.zaladuj("wilki.txt");
		for (Ssak s :
				tmp) {
			System.out.println(s.przedstawSie());
		}
	}

	private static class Ssak {
		private String imie;
		private short rokUrodzenia;
		private boolean mlody;

		public Ssak(String imie,short rokUrodzenia,boolean mlody) {
			this.imie = imie;
			this.rokUrodzenia = rokUrodzenia;
			this.mlody = mlody;
		}

		public String przedstawSie() {
			return imie + "," + rokUrodzenia + "," + mlody;
		}

		public void zapisz(FileOutputStream fos){
			try {
				fos.write(przedstawSie().replace(";", "\n").getBytes());
				fos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public static Ssak[] zaladuj(String sciezka){
			try {
				BufferedReader br = new BufferedReader(new FileReader(sciezka));
				int counter = 0;
				Wadera mama = null;
				String linia;
				Ssak[] ssaki = new Ssak[0];
				while ((linia = br.readLine()) != null){
					String[] dane = linia.split(",");
					Ssak[] tmp = new Ssak[ssaki.length + 1];
					for (int i = 0; i < ssaki.length; i++) {
						tmp[i] = ssaki[i];
					}
					switch (dane.length) {
						case 3 -> {
							tmp[tmp.length - 1] = new Ssak(dane[0], Short.parseShort(dane[1]), Boolean.parseBoolean(dane[2]));
							if(counter > 0) {
								mama.dodajSzczeniaka(tmp[tmp.length - 1]);
								counter--;
							}
						}
						case 4 -> {
							Wadera w = new Wadera(dane[0], Short.parseShort(dane[1]), Boolean.parseBoolean(dane[2]), Integer.parseInt(dane[3]));
							tmp[tmp.length - 1] = w;
							if(Integer.parseInt(dane[3]) > 0) {
								counter = Integer.parseInt(dane[3]);
								mama = w;
							} else if(counter > 0) {
								mama.dodajSzczeniaka(tmp[tmp.length - 1]);
								counter--;
							}
						}
						case 5 -> {
							tmp[tmp.length - 1] = new Wilk(dane[0], Short.parseShort(dane[1]), Boolean.parseBoolean(dane[2]), dane[3], Integer.parseInt(dane[4]));
							if(counter > 0) {
								mama.dodajSzczeniaka(tmp[tmp.length - 1]);
								counter--;
							}
						}
						default -> {}
					}
					ssaki = new Ssak[tmp.length];
					for (int i = 0; i < tmp.length; i++) {
						ssaki[i] = tmp[i];
					}
				}
				return ssaki;
			} catch (FileNotFoundException e) {
				Ssak[] ssaki = {
						new Wilk("Mateusz", (short) 2015, false, "Gryfindor", 3),
						new Wilk("Aleks", (short) 2022, true, "Gryfindor", 2),
						new Wilk("Tomasz", (short) 2010, false, "Gryfindor", 1),

						new Wadera("Aleksandra", (short) 2021, true),
						new Wadera("Karolina", (short) 2017, false),
						new Wadera("Alicja", (short) 2013, false),

						new Wilk("Julian", (short) 2018, false, "Sliterin", 3),
						new Wilk("Patryk", (short) 2020, true, "Sliterin", 2),
						new Wilk("Andrzej", (short) 2008, false, "Sliterin", 1),

						new Wadera("Patrycja", (short) 2021, true),
						new Wadera("Eugenia", (short) 2016, false),
						new Wadera("Zofia", (short) 2012, false),
				};
				for (int i = 0; i < ssaki.length; i++) {
					Ssak s = ssaki[i];
					try {
						s.zapisz(new FileOutputStream(sciezka, true));
					} catch (FileNotFoundException ex) {
						throw new RuntimeException(ex);
					}
				}
				return zaladuj(sciezka);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class Wadera extends Ssak {
		private int iloscSzczeniat;
		private Ssak[] szczenieta;

		public Wadera(String imie, short rokUrodzenia, boolean mlody) {
			super(imie, rokUrodzenia, mlody);
			//losujemy czy ma młode, dla uproszczenia zakładam, że wadera zachodzi w ciążę raz w życiu z ~50% prawdopodobieństwem zajścia takiego zdarzenia
			if(!mlody && Math.random() > 0.5) {
				// w miocie jest zazwyczaj 4-6 młodych
				this.szczenieta = new Ssak[(short)(Math.random() * 5 + 3)];
				for(int i = 0, j = szczenieta.length-1; i <= j;) {
					if(Math.random() > 0.5) {
						// wadery osiągają dojrzałość płciową średnio w wieku 2 lat, a pozostają płodne do 10 roku życia.
						szczenieta[j--] = new Wilk(imie + "MłodyWilk" + (j+1), (short) (rokUrodzenia + Math.random() * 10 + 2), true, "brak stada", 0);
					}else {
						szczenieta[i++] = new Wadera(imie + "MłodaWadera" + (i-1), (short)(rokUrodzenia + Math.random() * 10 + 2),true);
					}
				}
			} else
				szczenieta = new Ssak[0];
			this.iloscSzczeniat = szczenieta.length;
		}

		//konstruktor używany to wczytania z pliku
		public Wadera(String imie, short rokUrodzenia, boolean mlody, int iloscSzczeniat) {
			super(imie, rokUrodzenia, mlody);
			this.iloscSzczeniat = iloscSzczeniat;
			szczenieta = new Ssak[0];
		}

		public void dodajSzczeniaka(Ssak s){
			for (int i = 0; i < szczenieta.length; i++) {
				if(szczenieta[i] == null) {
					szczenieta[i] = s;
					return;
				}
			}
		}

		@Override
		public void zapisz(FileOutputStream fos){
			try {
				fos.write(przedstawSie().replace(";", "\n").getBytes());
				for (int i = 0; i < szczenieta.length; i++) {
					fos.write(szczenieta[i].przedstawSie().replace(";", "\n").getBytes());
				}
				fos.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String przedstawSie() {
			String s = super.przedstawSie() + "," + iloscSzczeniat + ";";
			return s;
		}
	}

	private static class Wilk extends Ssak {
		private String nazwaStada;
		private int pozycja;

		public Wilk(String imie, short rokUrodzenia, boolean mlody, String nazwaStada, int pozycja) {
			super(imie, rokUrodzenia, mlody);
			this.nazwaStada = nazwaStada;
			this.pozycja = pozycja;
		}

		@Override
		public String przedstawSie() {
			return super.przedstawSie() + "," + nazwaStada + "," + pozycja + ";";
		}
	}
}

