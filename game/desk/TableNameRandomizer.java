package game.desk;

import java.util.Iterator;
import java.util.Random;

public class TableNameRandomizer
{
  private static final String[] names = { "Antlia", "Apus", "Aquarius", "Aquila", "Ara", "Aries", "Auriga", "Bootes", "Caelum", "Camelopardalis", "Cancer", "Canes Venatici", "Canis Major", "Canis Minor", "Capricornus", "Carina", "Cassiopeia", "Centaurus", "Cepheus", "Cetus", "Chamaeleon", "Circinus", "Columba", "Coma Berenices", "Corona Austrina", "Corona Borealis", "Corvus", "Crater", "Crux", "Cygnus", "Delphinus", "Dorado", "Draco", "Equuleus", "Eridanus", "Fornax", "Gemini", "Grus", "Hercules", "Horologium", "Hydra", "Hydrus", "Indus", "Lacerta", "Leo", "Leo Minor", "Lepus", "Libra", "Lupus", "Lynx", "Lyra", "Mensa", "Microscopium", "Monoceros", "Musca", "Norma", "Octans", "Ophiuchus", "Orion", "Pavo", "Pegasus", "Perseus", "Phoenix", "Pictor", "Pisces", "Piscis Austrinus", "Puppis", "Pyxis", "Reticulum", "Sagitta", "Sagittarius", "Scorpius", "Sculptor", "Scutum", "Serpens", "Sextans", "Taurus", "Telescopium", "Triangulum", "Triangulum Australe", "Tucana", "Ursa Major", "Ursa Minor", "Vela", "Virgo", "Volans", "Vulpecula", "Acamar", "Achernar", "Achird", "Acrux", "Acubens", "Adara", "Adhafera", "Adhil", "Agena", "Ain Al Rami", "Ain", "Al Anz", "Al Kalb Al Rai", "Al Minliar Al Asad", "Al Minliar Al Shuja", "Aladfar", "Alathfar", "Albaldah", "Albali", "Albireo", "Alchiba", "Alcor", "Alcyone", "Aldebaran", "Alderamin", "Aldhibah", "Alfecca Meridiana", "Alfirk", "Algenib", "Algieba", "Algol", "Algorab", "Alhena", "Alioth", "Alkaid", "Alkalurops", "Alkes", "Alkurhah", "Almaak", "Alnair", "Alnath", "Alnilam", "Alnitak", "Alniyat", "Alniyat", "Alphard", "Alphekka", "Alpheratz", "Alrai", "Alrisha", "Alsafi", "Alsciaukat", "Alshain", "Alshat", "Alsuhail", "Altair", "Altarf", "Alterf", "Aludra", "Alula Australis", "Alula Borealis", "Alya", "Alzirr", "Ancha", "Angetenar", "Ankaa", "Anser", "Antares", "Arcturus", "Arkab Posterior", "Arkab Prior", "Arneb", "Arrakis", "Ascella", "Asellus Australis", "Asellus Borealis", "Asellus Primus", "Asellus Secondus", "Asellus Tertius", "Asterope", "Atik", "Atlas", "Auva", "Avior", "Azelfafage", "Azha", "Azmidiske", "Baham", "Baten Kaitos", "Becrux", "Beid", "Bellatrix", "Betelgeuse", "Botein", "Brachium", "Canopus", "Capella", "Caph", "Castor", "Cebalrai", "Celaeno", "Chara", "Chort", "Cor Caroli", "Cursa", "Dabih", "Deneb Algedi", "Deneb Dulfim", "Deneb El Okab", "Deneb El Okab", "Deneb Kaitos Shemali", "Deneb", "Denebola", "Dheneb", "Diadem", "Diphda", "Double Double", "Dschubba", "Dsiban", "Dubhe", "Ed Asich", "Electra", "Elnath", "Enif", "Etamin", "Fomalhaut", "Fornacis", "Fum Al Samakah", "Furud", "Gacrux", "Gianfar", "Gienah Cygni", "Gienah Ghurab", "Gomeisa", "Gorgonea Quarta", "Gorgonea Secunda", "Gorgonea Tertia", "Graffias", "Grafias", "Grumium", "Hadar", "Haedi", "Hamal", "Hassaleh", "Head Of Hydrus", "Herschel", "Heze", "Hoedus Ii", "Homam", "Hyadum I", "Hyadum Ii", "Izar", "Jabbah", "Kaffaljidhma", "Kajam", "Kaus Australis", "Kaus Borealis", "Kaus Meridionalis", "Keid", "Kitalpha", "Kocab", "Kornephoros", "Kraz", "Kuma", "Lesath", "Maasym", "Maia", "Marfak", "Marfak", "Marfic", "Marfik", "Markab", "Matar", "Mebsuta", "Megrez", "Meissa", "Mekbuda", "Menkalinan", "Menkar", "Menkar", "Menkent", "Menkib", "Merak", "Merga", "Merope", "Mesarthim", "Metallah", "Miaplacidus", "Minkar", "Mintaka", "Mira", "Mirach", "Miram", "Mirphak", "Mizar", "Mufrid", "Muliphen", "Murzim", "Muscida", "Muscida", "Muscida", "Nair Al Saif", "Naos", "Nash", "Nashira", "Nekkar", "Nihal", "Nodus Secundus", "Nunki", "Nusakan", "Peacock", "Phad", "Phaet", "Pherkad Minor", "Pherkad", "Pleione", "Polaris Australis", "Polaris", "Pollux", "Porrima", "Praecipua", "Prima Giedi", "Procyon", "Propus", "Propus", "Propus", "Rana", "Ras Elased Australis", "Ras Elased Borealis", "Rasalgethi", "Rasalhague", "Rastaban", "Regulus", "Rigel Kentaurus", "Rigel", "Rijl Al Awwa", "Rotanev", "Ruchba", "Ruchbah", "Rukbat", "Sabik", "Sadalachbia", "Sadalmelik", "Sadalsuud", "Sadr", "Saiph", "Salm", "Sargas", "Sarin", "Sceptrum", "Scheat", "Secunda Giedi", "Segin", "Seginus", "Sham", "Sharatan", "Shaula", "Shedir", "Sheliak", "Sirius", "Situla", "Skat", "Spica", "Sterope Ii", "Sualocin", "Subra", "Suhail Al Muhlif", "Sulafat", "Syrma", "Tabit", "Talitha", "Tania Australis", "Tania Borealis", "Tarazed", "Taygeta", "Tegmen", "Tejat Posterior", "Terebellum", "Terebellum", "Terebellum", "Terebellum", "Thabit", "Theemim", "Thuban", "Torcularis", "Turais", "Tyl", "Unukalhai", "Vega", "Vindemiatrix", "Wasat", "Wezen", "Wezn", "Yed Posterior", "Yed Prior", "Yildun", "Zaniah", "Zaurak", "Zavijah", "Zibal", "Zosma", "Zuben Elakrab", "Zuben Elakribi", "Zuben Elgenubi", "Zuben Elschemali" };

  public static String getRandomString()
  {
    Random randomizer = new Random();
    return names[randomizer.nextInt(names.length - 1)];
  }

  public Iterator iterator() {
    return new NamesIterator(null);
  }

  private class NamesIterator implements Iterator {
    private int current = 0;

    private NamesIterator() {
    }
    public void remove() {
    }
    public boolean hasNext() { current += 1;
      if (current >= TableNameRandomizer.names.length - 1) {
        current = 0;
      }
      return true; }

    public Object next()
    {
      return TableNameRandomizer.names[current];
    }
  }
}