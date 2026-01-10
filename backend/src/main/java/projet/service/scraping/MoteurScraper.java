package projet.service.scraping;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
@Service
public class MoteurScraper {

    // ==========================================
    // 🔧 USER CONFIGURATION
    // ==========================================
    private static final String BASE_URL = "https://www.moteur.ma/fr/voiture/achat-voiture-occasion/recherche/?marque=bmw&modele=serie+5";
    private static final String CSV_FILE = "bmw_serie-5.csv";
    // ==========================================

    private static final String SEPARATOR = ";";

    public static void main(String[] args) {

        System.out.println("🚀 Starting Scraper (Smart Pagination)...");
        System.out.println("Target: " + BASE_URL);

        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, false))) {
            writer.println("Titre;Prix;Annee;Kilometrage;Boite;Carburant;Ville;Image;Lien");

            // --- STEP 1: DETECT PAGES ---
            System.out.println("🔍 Detecting pages...");
            int maxPages = detectTotalPages(BASE_URL);

            // Safety cap: If it detects absurdly high number (like 270), cap it at 50 and rely on isEmpty check
            if (maxPages > 50) {
                System.out.println("⚠️ Warning: Detected " + maxPages + " pages. This seems too high (likely total car count). Capping loop at 50 and enabling auto-stop.");
                maxPages = 50;
            } else {
                System.out.println("💡 Detected " + maxPages + " pages.");
            }

            // --- STEP 2: LOOP ---
            for (int page = 1; page <= maxPages; page++) {

                String currentUrl = BASE_URL + "&page=" + page;
                System.out.println("\n--- PAGE " + page + " ---");

                Document listDoc = Jsoup.connect(currentUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(30000)
                        .get();

                Elements cars = listDoc.select("div.row-item");
                if (cars.isEmpty()) cars = listDoc.select("div.offers-list > div");

                // CRITICAL STOP: If no cars found, stop immediately regardless of maxPages
                if (cars.isEmpty()) {
                    System.out.println("🛑 No ads found on page " + page + ". Stopping scraper.");
                    break;
                }

                int count = 0;
                for (Element carCard : cars) {
                    count++;
                    try {
                        // 1. Title
                        String title = carCard.select("h3.title a").text().trim();
                        if (title.isEmpty()) title = carCard.select(".product-title a").text().trim();
                        if (title.isEmpty()) title = carCard.select("h3").text().trim();

                        // 2. Price
                        String rawPrice = carCard.select("div.price").text();
                        String price = rawPrice.replaceAll("[^0-9]", "");
                        if (price.isEmpty()) price = "0";

                        // 3. City
                        String city = "N/A";
                        Element metaDiv = carCard.selectFirst("div.meta");
                        if (metaDiv != null) {
                            Element pointerIcon = metaDiv.select("i.icon-normal-pointer").first();
                            if (pointerIcon != null) city = pointerIcon.parent().text().trim();
                        }
                        if (city.equals("N/A")) city = carCard.select("span.city").text().trim();

                        // 4. Link
                        Element linkEl = carCard.select("h3.title a").first();
                        if (linkEl == null) linkEl = carCard.select("a[href*='detail-annonce']").first();
                        if (linkEl == null) continue;

                        String detailLink = linkEl.attr("href");
                        if (!detailLink.startsWith("http")) detailLink = "https://www.moteur.ma" + detailLink;

                        // 5. Details
                        System.out.print("   [" + count + "] Fetching... ");
                        Thread.sleep(1000);

                        Document detailDoc = Jsoup.connect(detailLink)
                                .userAgent("Mozilla/5.0")
                                .timeout(30000)
                                .get();

                        String year = findSpec(detailDoc, "Année");
                        String km = findSpec(detailDoc, "Kilométrage");
                        String fuel = findSpec(detailDoc, "Carburant");
                        String boxRaw = findGearbox(detailDoc);

                        String box = "N/A";
                        if (!boxRaw.equals("N/A")) {
                            String lowerBox = boxRaw.toLowerCase();
                            if (lowerBox.contains("manuelle")) box = "Manuelle";
                            else if (lowerBox.contains("automatique") || lowerBox.contains("auto")) box = "Automatique";
                            else box = boxRaw;
                        }

                        String imageUrl = detailDoc.select("meta[property='og:image']").attr("content");

                        System.out.println("✅ " + title + " | " + box);

                        writer.println(
                                clean(title) + SEPARATOR +
                                        clean(price) + SEPARATOR +
                                        clean(year) + SEPARATOR +
                                        clean(km) + SEPARATOR +
                                        clean(box) + SEPARATOR +
                                        clean(fuel) + SEPARATOR +
                                        clean(city) + SEPARATOR +
                                        clean(imageUrl) + SEPARATOR +
                                        detailLink
                        );
                        writer.flush();

                    } catch (Exception e) {
                        System.err.println("❌ Error: " + e.getMessage());
                    }
                }
            } // End Loop

            System.out.println("\n🏁 Done! Saved to " + CSV_FILE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Improved detection logic.
     */
    private static int detectTotalPages(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(30000).get();

            // STRATEGY 1: Look for "Page X sur Y" or "Page X / Y" text
            // Moteur.ma often puts this in a text node inside the pagination container
            Elements textNodes = doc.getElementsContainingOwnText("Page");
            for (Element el : textNodes) {
                // Matches "Page 1 sur 15" or "Page 1/15"
                if (el.text().matches(".*Page \\d+.*\\d+.*")) {
                    String clean = el.text().replaceAll("[^0-9/sur]", ""); // Keep numbers and separators
                    String[] parts = clean.split("(sur|/)");
                    if (parts.length > 1) {
                        try {
                            return Integer.parseInt(parts[1].trim());
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            // STRATEGY 2: Check standard links, but ignore huge numbers
            Elements pageLinks = doc.select(".pagination a");
            int maxPage = 1;
            for (Element link : pageLinks) {
                String text = link.text().trim();
                if (text.matches("\\d+")) {
                    int p = Integer.parseInt(text);
                    // If page number > 100, it's likely a car count, ignore it
                    if (p < 100) {
                        maxPage = Math.max(maxPage, p);
                    }
                }
            }
            return maxPage;

        } catch (IOException e) {
            return 1;
        }
    }

    private static String findGearbox(Document doc) {
        Elements lines = doc.select("div.box div.detail_line");
        for (Element line : lines) {
            if (line.text().toLowerCase().contains("boîte") || line.text().toLowerCase().contains("transmission")) {
                Element boldSpan = line.selectFirst("span.text_bold");
                if (boldSpan != null) return boldSpan.text().trim();
            }
        }
        Elements boldSpans = doc.select("div.box span.text_bold");
        for (Element span : boldSpans) {
            String text = span.text().toLowerCase();
            if (text.contains("manuelle")) return "Manuelle";
            if (text.contains("automatique")) return "Automatique";
        }
        return "N/A";
    }

    private static String findSpec(Document doc, String label) {
        Elements lines = doc.select("div.detail_line");
        for (Element line : lines) {
            if (line.text().toLowerCase().contains(label.toLowerCase())) {
                return line.text().replaceFirst("(?i)" + label, "").replace(":", "").trim();
            }
        }
        Elements labels = doc.getElementsContainingOwnText(label);
        for (Element el : labels) {
            if (el.nextElementSibling() != null) return el.nextElementSibling().text().trim();
            if (el.parent() != null) return el.parent().text().replace(label, "").replace(":", "").replace("de mise en circulation", "").trim();
        }
        return "N/A";
    }

    private static String clean(String s) {
        if (s == null) return "N/A";
        return s.replace(";", "").replace("\n", "").trim();
    }
}