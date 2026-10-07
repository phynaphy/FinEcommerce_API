package com.example.FIN_ecommerce_API.service.serviceImpl;
import com.example.FIN_ecommerce_API.dto.report.*;
import com.example.FIN_ecommerce_API.model.*;
import com.example.FIN_ecommerce_API.repository.*;
import com.example.FIN_ecommerce_API.service.AdminReportService;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReportServiceImpl implements AdminReportService {

    private static final int LOW_STOCK_LIMIT = 5;

    // Palette Colors
    private static final Color PRIMARY_COLOR = new Color(30, 41, 59);    // Slate 800
    private static final Color SECONDARY_COLOR = new Color(71, 85, 105); // Slate 600
    private static final Color HEADER_BG = new Color(241, 245, 249);     // Slate 100
    private static final Color ROW_ALT_BG = new Color(248, 250, 252);    // Slate 50
    private static final Color BORDER_COLOR = new Color(226, 232, 240);   // Slate 200

    // Fonts
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA, 10, SECONDARY_COLOR);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, PRIMARY_COLOR);
    private static final Font FONT_CELL = FontFactory.getFont(FontFactory.HELVETICA, 9, PRIMARY_COLOR);
    private static final Font FONT_CELL_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, PRIMARY_COLOR);

    private final AdminReportUserRepository userRepository;
    private final AdminReportProductRepository productRepository;
    private final AdminReportCategoryRepository categoryRepository;
    private final AdminReportVariantRepository variantRepository;
    private final AdminReportCartRepository cartRepository;
    private final AdminReportCartItemRepository cartItemRepository;

    @Override
    public DashboardReportDto getDashboard() {
        Long stock = variantRepository.sumStockQuantity();
        Long quantity = cartRepository.sumCartQuantity();
        BigDecimal value = cartRepository.sumCartValue();

        return DashboardReportDto.builder()
                .totalUsers(userRepository.count())
                .totalAdminUsers(userRepository.countAdminUsers())
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .totalVariants(variantRepository.count())
                .totalStock(stock == null ? 0 : stock)
                .lowStockVariants(variantRepository.countByStockQuantityLessThanEqual(LOW_STOCK_LIMIT))
                .totalCarts(cartRepository.count())
                .totalCartItems(cartRepository.countCartItems())
                .totalCartQuantity(quantity == null ? 0 : quantity)
                .cartValue(value == null ? BigDecimal.ZERO : value)
                .build();
    }

    @Override
    public List<UserReportDto> getUsers() {
        return userRepository.findAll().stream().map(u -> UserReportDto.builder()
                .id(u.getId()).username(u.getUsername()).email(u.getEmail())
                .fullName(u.getFullName())
                .role(u.getRole() == null ? null : u.getRole().name())
                .build()).toList();
    }

    @Override
    public List<ProductReportDto> getProducts() {
        return productRepository.findAllForReport().stream().map(p -> ProductReportDto.builder()
                .id(p.getId()).name(p.getName())
                .categoryName(p.getCategory() == null ? null : p.getCategory().getName())
                .price(p.getPrice()).originalPrice(p.getOriginalPrice())
                .discountPercentage(p.getDiscountPercentage()).rating(p.getRating())
                .reviewCount(p.getReviewCount()).officialStore(p.getIsOfficialStore())
                .variantCount(p.getVariants() == null ? 0 : p.getVariants().size())
                .build()).toList();
    }

    @Override
    public List<CategoryReportDto> getCategories() {
        return categoryRepository.getCategoryReport().stream()
                .map(r -> CategoryReportDto.builder()
                        .id((Long) r[0]).name((String) r[1])
                        .productCount(((Number) r[2]).longValue()).build())
                .toList();
    }

    @Override
    public List<VariantReportDto> getVariants() {
        return variantRepository.findAllForReport().stream().map(this::mapVariant).toList();
    }

    private VariantReportDto mapVariant(ProductVariant v) {
        int stock = v.getStockQuantity() == null ? 0 : v.getStockQuantity();
        String status = stock <= 0 ? "OUT_OF_STOCK" : stock <= LOW_STOCK_LIMIT ? "LOW_STOCK" : "IN_STOCK";
        return VariantReportDto.builder()
                .id(v.getId()).productId(v.getProduct().getId()).productName(v.getProduct().getName())
                .color(v.getColor()).colorHex(v.getColorHex()).storage(v.getStorage())
                .priceAdjustment(v.getPriceAdjustment()).stockQuantity(v.getStockQuantity())
                .stockStatus(status).build();
    }

    @Override
    public List<CartItemReportDto> getCartItems() {
        return cartItemRepository.findAllForReport().stream().map(this::mapCartItem).toList();
    }

    private CartItemReportDto mapCartItem(CartItem i) {
        BigDecimal subtotal = i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
        ProductVariant v = i.getVariant();
        return CartItemReportDto.builder()
                .cartItemId(i.getId()).cartId(i.getCart().getId())
                .userId(i.getCart().getUser().getId()).username(i.getCart().getUser().getUsername())
                .productId(i.getProduct().getId()).productName(i.getProduct().getName())
                .variantId(v == null ? null : v.getId())
                .color(v == null ? null : v.getColor())
                .storage(v == null ? null : v.getStorage())
                .quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).subtotal(subtotal)
                .build();
    }

    // ================= EXCEL EXPORT =================

    @Override
    public byte[] exportExcel() {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle header = headerStyle(wb);
            CellStyle money = moneyStyle(wb);
            dashboardSheet(wb, header, money, getDashboard());
            usersSheet(wb, header, getUsers());
            productsSheet(wb, header, money, getProducts());
            categoriesSheet(wb, header, getCategories());
            variantsSheet(wb, header, money, getVariants());
            cartItemsSheet(wb, header, money, getCartItems());
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Could not generate Excel report", e);
        }
    }

    private CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font f = wb.createFont(); f.setBold(true); s.setFont(f);
        return s;
    }

    private CellStyle moneyStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        return s;
    }

    private void dashboardSheet(Workbook wb, CellStyle h, CellStyle m, DashboardReportDto d) {
        Sheet s = wb.createSheet("Dashboard");
        String[] headers = {"Metric", "Value"};
        writeHeader(s, headers, h);
        Object[][] rows = {
                {"Total Users", d.getTotalUsers()}, {"Admin Users", d.getTotalAdminUsers()},
                {"Total Products", d.getTotalProducts()}, {"Total Categories", d.getTotalCategories()},
                {"Total Variants", d.getTotalVariants()}, {"Total Stock", d.getTotalStock()},
                {"Low Stock Variants", d.getLowStockVariants()}, {"Total Carts", d.getTotalCarts()},
                {"Cart Item Records", d.getTotalCartItems()}, {"Cart Quantity", d.getTotalCartQuantity()},
                {"Cart Value", d.getCartValue()}
        };
        for (int r = 0; r < rows.length; r++) {
            org.apache.poi.ss.usermodel.Row row = s.createRow(r + 1);
            set(row, 0, rows[r][0]);
            if (rows[r][1] instanceof BigDecimal b) setMoney(row, 1, b, m); else set(row, 1, rows[r][1]);
        }
        autoSize(s, 2);
    }

    private void usersSheet(Workbook wb, CellStyle h, List<UserReportDto> data) {
        Sheet s = wb.createSheet("Users"); String[] hs = {"ID","Username","Email","Full Name","Role"}; writeHeader(s, hs, h);
        int r = 1; for (UserReportDto d : data) { org.apache.poi.ss.usermodel.Row x=s.createRow(r++); set(x,0,d.getId()); set(x,1,d.getUsername()); set(x,2,d.getEmail()); set(x,3,d.getFullName()); set(x,4,d.getRole()); } autoSize(s,hs.length);
    }

    private void productsSheet(Workbook wb, CellStyle h, CellStyle m, List<ProductReportDto> data) {
        Sheet s=wb.createSheet("Products"); String[] hs={"ID","Product Name","Category","Price","Original Price","Discount %","Rating","Review Count","Official Store","Variant Count"}; writeHeader(s,hs,h);
        int r=1; for(ProductReportDto d:data){org.apache.poi.ss.usermodel.Row x=s.createRow(r++); set(x,0,d.getId());set(x,1,d.getName());set(x,2,d.getCategoryName());setMoney(x,3,d.getPrice(),m);setMoney(x,4,d.getOriginalPrice(),m);set(x,5,d.getDiscountPercentage());set(x,6,d.getRating());set(x,7,d.getReviewCount());set(x,8,d.getOfficialStore());set(x,9,d.getVariantCount());} autoSize(s,hs.length);
    }

    private void categoriesSheet(Workbook wb, CellStyle h, List<CategoryReportDto> data) {
        Sheet s=wb.createSheet("Categories"); String[] hs={"ID","Category","Product Count"}; writeHeader(s,hs,h);
        int r=1; for(CategoryReportDto d:data){org.apache.poi.ss.usermodel.Row x=s.createRow(r++);set(x,0,d.getId());set(x,1,d.getName());set(x,2,d.getProductCount());} autoSize(s,hs.length);
    }

    private void variantsSheet(Workbook wb, CellStyle h, CellStyle m, List<VariantReportDto> data) {
        Sheet s=wb.createSheet("Variants"); String[] hs={"ID","Product ID","Product","Color","Color Hex","Storage","Price Adjustment","Stock Quantity","Stock Status"}; writeHeader(s,hs,h);
        int r=1; for(VariantReportDto d:data){org.apache.poi.ss.usermodel.Row x=s.createRow(r++);set(x,0,d.getId());set(x,1,d.getProductId());set(x,2,d.getProductName());set(x,3,d.getColor());set(x,4,d.getColorHex());set(x,5,d.getStorage());setMoney(x,6,d.getPriceAdjustment(),m);set(x,7,d.getStockQuantity());set(x,8,d.getStockStatus());} autoSize(s,hs.length);
    }

    private void cartItemsSheet(Workbook wb, CellStyle h, CellStyle m, List<CartItemReportDto> data) {
        Sheet s=wb.createSheet("Cart Items"); String[] hs={"Cart Item ID","Cart ID","User ID","Username","Product ID","Product","Variant ID","Color","Storage","Quantity","Unit Price","Subtotal"}; writeHeader(s,hs,h);
        int r=1; for(CartItemReportDto d:data){org.apache.poi.ss.usermodel.Row x=s.createRow(r++);set(x,0,d.getCartItemId());set(x,1,d.getCartId());set(x,2,d.getUserId());set(x,3,d.getUsername());set(x,4,d.getProductId());set(x,5,d.getProductName());set(x,6,d.getVariantId());set(x,7,d.getColor());set(x,8,d.getStorage());set(x,9,d.getQuantity());setMoney(x,10,d.getUnitPrice(),m);setMoney(x,11,d.getSubtotal(),m);} autoSize(s,hs.length);
    }

    private void writeHeader(Sheet s,String[] hs,CellStyle style){org.apache.poi.ss.usermodel.Row r=s.createRow(0);for(int i=0;i<hs.length;i++){org.apache.poi.ss.usermodel.Cell c=r.createCell(i);c.setCellValue(hs[i]);c.setCellStyle(style);}}
    private void set(org.apache.poi.ss.usermodel.Row r,int c,Object v){org.apache.poi.ss.usermodel.Cell x=r.createCell(c);if(v==null)x.setBlank();else if(v instanceof Number n)x.setCellValue(n.doubleValue());else if(v instanceof Boolean b)x.setCellValue(b);else x.setCellValue(String.valueOf(v));}
    private void setMoney(org.apache.poi.ss.usermodel.Row r,int c,BigDecimal v,CellStyle s){org.apache.poi.ss.usermodel.Cell x=r.createCell(c);if(v!=null){x.setCellValue(v.doubleValue());x.setCellStyle(s);}}
    private void autoSize(Sheet s,int n){for(int i=0;i<n;i++)s.autoSizeColumn(i);}

    // ================= STYLED PDF EXPORT IMPLEMENTATION =================

    @Override
    public byte[] exportPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Executive E-Commerce Report Summary");
            renderDashboardSection(doc, getDashboard());
            doc.newPage();
            renderHeader(doc, "Users Directory Report");
            renderUsersSection(doc, getUsers());
            doc.newPage();
            renderHeader(doc, "Products Catalog Report");
            renderProductsSection(doc, getProducts());
            doc.newPage();
            renderHeader(doc, "Categories Overview");
            renderCategoriesSection(doc, getCategories());
            doc.newPage();
            renderHeader(doc, "Product Variants & Inventory");
            renderVariantsSection(doc, getVariants());
            doc.newPage();
            renderHeader(doc, "Active Shopping Cart Items");
            renderCartItemsSection(doc, getCartItems());
        });
    }

    @Override
    public byte[] exportDashboardPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Dashboard Summary Report");
            renderDashboardSection(doc, getDashboard());
        });
    }

    @Override
    public byte[] exportUsersPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Users Directory Report");
            renderUsersSection(doc, getUsers());
        });
    }

    @Override
    public byte[] exportProductsPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Products Catalog Report");
            renderProductsSection(doc, getProducts());
        });
    }

    @Override
    public byte[] exportCategoriesPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Categories Overview");
            renderCategoriesSection(doc, getCategories());
        });
    }

    @Override
    public byte[] exportVariantsPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Product Variants Report");
            renderVariantsSection(doc, getVariants());
        });
    }

    @Override
    public byte[] exportCartItemsPdf() {
        return buildPdf((doc, writer) -> {
            renderHeader(doc, "Active Cart Items Report");
            renderCartItemsSection(doc, getCartItems());
        });
    }

    @FunctionalInterface
    private interface PdfContentWriter {
        void write(Document doc, PdfWriter writer) throws DocumentException;
    }

    private byte[] buildPdf(PdfContentWriter contentWriter) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(doc, out);

            // Add Header/Footer Event Handler
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter w, Document d) {
                    PdfContentByte cb = w.getDirectContent();
                    cb.setColorStroke(BORDER_COLOR);
                    cb.setLineWidth(0.5f);
                    cb.moveTo(36, 30);
                    cb.lineTo(d.getPageSize().getWidth() - 36, 30);
                    cb.stroke();

                    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                            new Phrase(String.format("Page %d", w.getPageNumber()), FONT_SUBTITLE),
                            d.getPageSize().getWidth() - 36, 18, 0);

                    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                            new Phrase("FIN E-Commerce API Reports", FONT_SUBTITLE),
                            36, 18, 0);
                }
            });

            doc.open();
            contentWriter.write(doc, writer);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Could not generate PDF report", e);
        }
    }

    // --- Section Renderers ---

    private void renderHeader(Document doc, String title) throws DocumentException {
        Paragraph pTitle = new Paragraph(title, FONT_TITLE);
        pTitle.setSpacingAfter(4);
        doc.add(pTitle);

        Paragraph pSub = new Paragraph("Generated automatically by Admin Reporting Service", FONT_SUBTITLE);
        pSub.setSpacingAfter(15);
        doc.add(pSub);
    }

    private void renderDashboardSection(Document doc, DashboardReportDto d) throws DocumentException {
        PdfPTable table = createTable(new float[]{3f, 2f});
        addTableHeader(table, new String[]{"Key Performance Indicator", "Value"});

        boolean alt = false;
        alt = addTableRow(table, alt, "Total Users", String.valueOf(d.getTotalUsers()));
        alt = addTableRow(table, alt, "Total Admin Users", String.valueOf(d.getTotalAdminUsers()));
        alt = addTableRow(table, alt, "Total Products Cataloged", String.valueOf(d.getTotalProducts()));
        alt = addTableRow(table, alt, "Total Product Categories", String.valueOf(d.getTotalCategories()));
        alt = addTableRow(table, alt, "Total Product Variants", String.valueOf(d.getTotalVariants()));
        alt = addTableRow(table, alt, "Total Stock Quantity", String.valueOf(d.getTotalStock()));
        alt = addTableRow(table, alt, "Low Stock Alert Variants", String.valueOf(d.getLowStockVariants()));
        alt = addTableRow(table, alt, "Active Carts Count", String.valueOf(d.getTotalCarts()));
        alt = addTableRow(table, alt, "Total Cart Items", String.valueOf(d.getTotalCartItems()));
        alt = addTableRow(table, alt, "Total Items Quantity in Carts", String.valueOf(d.getTotalCartQuantity()));
        addTableRow(table, alt, "Total Cart Value", formatMoney(d.getCartValue()));

        doc.add(table);
    }

    private void renderUsersSection(Document doc, List<UserReportDto> users) throws DocumentException {
        PdfPTable table = createTable(new float[]{1f, 2f, 3f, 2.5f, 1.5f});
        addTableHeader(table, new String[]{"ID", "Username", "Email", "Full Name", "Role"});

        boolean alt = false;
        for (UserReportDto u : users) {
            alt = addTableRow(table, alt,
                    String.valueOf(u.getId()),
                    u.getUsername(),
                    u.getEmail(),
                    u.getFullName(),
                    u.getRole()
            );
        }
        doc.add(table);
    }

    private void renderProductsSection(Document doc, List<ProductReportDto> products) throws DocumentException {
        PdfPTable table = createTable(new float[]{0.8f, 3f, 2f, 1.5f, 1f, 1.2f});
        addTableHeader(table, new String[]{"ID", "Product Name", "Category", "Price", "Rating", "Variants"});

        boolean alt = false;
        for (ProductReportDto p : products) {
            alt = addTableRow(table, alt,
                    String.valueOf(p.getId()),
                    p.getName(),
                    p.getCategoryName(),
                    formatMoney(p.getPrice()),
                    p.getRating() != null ? p.getRating() + " ★" : "-",
                    String.valueOf(p.getVariantCount())
            );
        }
        doc.add(table);
    }

    private void renderCategoriesSection(Document doc, List<CategoryReportDto> categories) throws DocumentException {
        PdfPTable table = createTable(new float[]{1f, 4f, 2f});
        addTableHeader(table, new String[]{"ID", "Category Name", "Total Products"});

        boolean alt = false;
        for (CategoryReportDto c : categories) {
            alt = addTableRow(table, alt,
                    String.valueOf(c.getId()),
                    c.getName(),
                    String.valueOf(c.getProductCount())
            );
        }
        doc.add(table);
    }

    private void renderVariantsSection(Document doc, List<VariantReportDto> variants) throws DocumentException {
        PdfPTable table = createTable(new float[]{0.8f, 3f, 1.5f, 1.5f, 1.2f, 1.8f});
        addTableHeader(table, new String[]{"ID", "Product Name", "Color", "Storage", "Stock", "Status"});

        boolean alt = false;
        for (VariantReportDto v : variants) {
            alt = addTableRow(table, alt,
                    String.valueOf(v.getId()),
                    v.getProductName(),
                    v.getColor(),
                    v.getStorage(),
                    String.valueOf(v.getStockQuantity()),
                    v.getStockStatus()
            );
        }
        doc.add(table);
    }

    private void renderCartItemsSection(Document doc, List<CartItemReportDto> cartItems) throws DocumentException {
        PdfPTable table = createTable(new float[]{0.8f, 2f, 3f, 1f, 1.5f, 1.5f});
        addTableHeader(table, new String[]{"ID", "Username", "Product", "Qty", "Unit Price", "Subtotal"});

        boolean alt = false;
        for (CartItemReportDto i : cartItems) {
            alt = addTableRow(table, alt,
                    String.valueOf(i.getCartItemId()),
                    i.getUsername(),
                    i.getProductName(),
                    String.valueOf(i.getQuantity()),
                    formatMoney(i.getUnitPrice()),
                    formatMoney(i.getSubtotal())
            );
        }
        doc.add(table);
    }

    // --- Table Helper Methods ---

    private PdfPTable createTable(float[] columnWidths) {
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(8f);
        table.setSpacingAfter(15f);
        return table;
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FONT_HEADER));
            cell.setBackgroundColor(HEADER_BG);
            cell.setBorderColor(BORDER_COLOR);
            cell.setPaddingTop(8);
            cell.setPaddingBottom(8);
            cell.setPaddingLeft(6);
            cell.setPaddingRight(6);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }
        table.setHeaderRows(1);
    }

    private boolean addTableRow(PdfPTable table, boolean alt, String... values) {
        Color bg = alt ? ROW_ALT_BG : Color.WHITE;
        for (String val : values) {
            PdfPCell cell = new PdfPCell(new Phrase(val == null ? "" : val, FONT_CELL));
            cell.setBackgroundColor(bg);
            cell.setBorderColor(BORDER_COLOR);
            cell.setPaddingTop(6);
            cell.setPaddingBottom(6);
            cell.setPaddingLeft(6);
            cell.setPaddingRight(6);
            table.addCell(cell);
        }
        return !alt;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "$0.00";
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(amount);
    }
}