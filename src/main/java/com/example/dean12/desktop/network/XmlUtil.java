package com.example.dean12.desktop.network;

import com.example.dean12.model.SinhVien;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XmlUtil {

    public static String exportStudentsToXml(List<SinhVien> list) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("students");
        doc.appendChild(root);

        for (SinhVien sv : list) {
            Element student = doc.createElement("student");

            Element maSV = doc.createElement("ma_sv");
            maSV.setTextContent(sv.getMaSV());
            student.appendChild(maSV);

            Element hoTen = doc.createElement("ho_ten");
            hoTen.setTextContent(sv.getHoTen());
            student.appendChild(hoTen);

            Element lop = doc.createElement("lop");
            lop.setTextContent(sv.getLop() != null ? sv.getLop() : "");
            student.appendChild(lop);

            Element email = doc.createElement("email");
            email.setTextContent(sv.getEmail() != null ? sv.getEmail() : "");
            student.appendChild(email);

            Element sdt = doc.createElement("sdt");
            sdt.setTextContent(sv.getSdt() != null ? sv.getSdt() : "");
            student.appendChild(sdt);

            root.appendChild(student);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    public static List<SinhVien> importStudentsFromXml(String xmlData) throws Exception {
        List<SinhVien> list = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8))) {
            Document doc = db.parse(bais);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("student");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element elem = (Element) nodeList.item(i);

                String maSV = getElementValue(elem, "ma_sv");
                String hoTen = getElementValue(elem, "ho_ten");
                String lop = getElementValue(elem, "lop");
                String email = getElementValue(elem, "email");
                String sdt = getElementValue(elem, "sdt");

                if (maSV == null || maSV.isEmpty() || hoTen == null || hoTen.isEmpty()) {
                    throw new IllegalArgumentException("Dữ liệu XML thiếu trường thông tin bắt buộc (ma_sv hoặc ho_ten) ở thẻ <student> thứ " + (i + 1));
                }

                SinhVien sv = new SinhVien();
                sv.setMaSV(maSV.toUpperCase().trim());
                sv.setHoTen(hoTen.trim());
                sv.setLop(lop != null ? lop.trim() : "");
                sv.setEmail(email != null ? email.trim() : "");
                sv.setSdt(sdt != null ? sdt.trim() : "");
                
                list.add(sv);
            }
        }
        return list;
    }

    private static String getElementValue(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return null;
    }
}
