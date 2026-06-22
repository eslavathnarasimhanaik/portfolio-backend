package com.naik.portfolio.controller;

import com.naik.portfolio.model.ContactMessage;
import com.naik.portfolio.model.MeetingBooking;
import com.naik.portfolio.repository.ContactMessageRepository;
import com.naik.portfolio.repository.MeetingBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Dashboard — View all database data at http://localhost:8080/admin
 * No login required. Shows contact messages and meeting bookings in a beautiful HTML table.
 */
@RestController
@CrossOrigin(origins = "*")
public class AdminController {

    private final ContactMessageRepository contactRepo;
    private final MeetingBookingRepository meetingRepo;

    @Autowired
    public AdminController(ContactMessageRepository contactRepo, MeetingBookingRepository meetingRepo) {
        this.contactRepo = contactRepo;
        this.meetingRepo = meetingRepo;
    }

    @GetMapping(value = "/admin", produces = "text/html")
    public String adminDashboard() {
        var contacts = contactRepo.findAll();
        var meetings = meetingRepo.findAll();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<title>Portfolio Admin Dashboard</title>");
        html.append("<style>");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("body { font-family: 'Segoe UI', system-ui, sans-serif; background: #0f172a; color: #e2e8f0; padding: 2rem; }");
        html.append("h1 { text-align: center; font-size: 2rem; margin-bottom: 0.5rem; background: linear-gradient(135deg, #818cf8, #6366f1); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }");
        html.append(".subtitle { text-align: center; color: #94a3b8; margin-bottom: 2rem; font-size: 0.9rem; }");
        html.append(".stats { display: flex; gap: 1rem; justify-content: center; margin-bottom: 2rem; }");
        html.append(".stat-card { background: rgba(99,102,241,0.1); border: 1px solid rgba(99,102,241,0.3); border-radius: 12px; padding: 1.2rem 2rem; text-align: center; }");
        html.append(".stat-num { font-size: 2rem; font-weight: 800; color: #818cf8; }");
        html.append(".stat-label { font-size: 0.8rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.05em; }");
        html.append("h2 { font-size: 1.3rem; margin: 2rem 0 1rem; color: #a5b4fc; border-bottom: 2px solid rgba(99,102,241,0.3); padding-bottom: 0.5rem; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 2rem; }");
        html.append("th { background: rgba(99,102,241,0.2); color: #c7d2fe; padding: 0.8rem 1rem; text-align: left; font-weight: 700; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.05em; }");
        html.append("td { padding: 0.7rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.05); font-size: 0.9rem; }");
        html.append("tr:hover td { background: rgba(99,102,241,0.05); }");
        html.append(".badge { display: inline-block; padding: 0.2rem 0.6rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 700; }");
        html.append(".badge-pending { background: rgba(251,191,36,0.15); color: #fbbf24; }");
        html.append(".badge-confirmed { background: rgba(52,211,153,0.15); color: #34d399; }");
        html.append(".empty { text-align: center; color: #64748b; padding: 2rem; font-style: italic; }");
        html.append(".refresh-btn { display: block; margin: 1rem auto; padding: 0.6rem 2rem; background: linear-gradient(135deg, #6366f1, #818cf8); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 0.9rem; font-weight: 600; text-decoration: none; text-align: center; width: fit-content; }");
        html.append(".refresh-btn:hover { opacity: 0.9; }");
        html.append(".db-info { text-align: center; margin-top: 1rem; padding: 1rem; background: rgba(52,211,153,0.05); border: 1px solid rgba(52,211,153,0.2); border-radius: 8px; font-size: 0.8rem; color: #94a3b8; }");
        html.append(".db-info code { background: rgba(99,102,241,0.15); padding: 0.15rem 0.4rem; border-radius: 4px; color: #c7d2fe; font-family: monospace; }");
        html.append("</style></head><body>");

        // Header
        html.append("<h1>🛡️ Portfolio Admin Dashboard</h1>");
        html.append("<p class='subtitle'>Real-time database viewer — Auto-refreshes on page reload</p>");

        // Stats
        html.append("<div class='stats'>");
        html.append("<div class='stat-card'><div class='stat-num'>").append(contacts.size()).append("</div><div class='stat-label'>Contact Messages</div></div>");
        html.append("<div class='stat-card'><div class='stat-num'>").append(meetings.size()).append("</div><div class='stat-label'>Meeting Bookings</div></div>");
        html.append("<div class='stat-card'><div class='stat-num'>").append(contacts.size() + meetings.size()).append("</div><div class='stat-label'>Total Records</div></div>");
        html.append("</div>");

        // Contact Messages Table
        html.append("<h2>✉️ Contact Messages</h2>");
        if (contacts.isEmpty()) {
            html.append("<div class='empty'>No contact messages yet. Submit one from the portfolio!</div>");
        } else {
            html.append("<table><tr><th>ID</th><th>Name</th><th>Email</th><th>Message</th><th>Submitted At</th></tr>");
            for (ContactMessage c : contacts) {
                html.append("<tr>");
                html.append("<td>").append(c.getId()).append("</td>");
                html.append("<td>").append(escapeHtml(c.getName())).append("</td>");
                html.append("<td>").append(escapeHtml(c.getEmail())).append("</td>");
                html.append("<td>").append(escapeHtml(c.getMessage())).append("</td>");
                html.append("<td>").append(c.getSubmittedAt() != null ? c.getSubmittedAt().toString().replace("T", " ") : "—").append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        // Meeting Bookings Table
        html.append("<h2>📅 Meeting Bookings</h2>");
        if (meetings.isEmpty()) {
            html.append("<div class='empty'>No meeting bookings yet. Book one from the portfolio!</div>");
        } else {
            html.append("<table><tr><th>ID</th><th>Name</th><th>Email</th><th>Topic</th><th>Date</th><th>Time</th><th>Status</th><th>Booked At</th></tr>");
            for (MeetingBooking m : meetings) {
                html.append("<tr>");
                html.append("<td>").append(m.getId()).append("</td>");
                html.append("<td>").append(escapeHtml(m.getName())).append("</td>");
                html.append("<td>").append(escapeHtml(m.getEmail())).append("</td>");
                html.append("<td>").append(escapeHtml(m.getTopic())).append("</td>");
                html.append("<td>").append(m.getPreferredDate() != null ? m.getPreferredDate() : "—").append("</td>");
                html.append("<td>").append(m.getPreferredTime() != null ? m.getPreferredTime() : "—").append("</td>");
                String statusClass = "CONFIRMED".equals(m.getStatus()) ? "badge-confirmed" : "badge-pending";
                html.append("<td><span class='badge ").append(statusClass).append("'>").append(m.getStatus()).append("</span></td>");
                html.append("<td>").append(m.getBookedAt() != null ? m.getBookedAt().toString().replace("T", " ") : "—").append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        // Refresh + H2 Console link
        html.append("<a href='/admin' class='refresh-btn'>🔄 Refresh Data</a>");
        html.append("<div class='db-info'>");
        html.append("<strong>H2 Console:</strong> <a href='/h2-console' style='color:#818cf8;'>localhost:8080/h2-console</a> &nbsp;|&nbsp; ");
        html.append("JDBC URL: <code>jdbc:h2:mem:portfolio_db</code> &nbsp;|&nbsp; User: <code>sa</code> &nbsp;|&nbsp; Password: <em>(empty)</em><br><br>");
        html.append("<strong>API Endpoints:</strong> ");
        html.append("<a href='/api/contact' style='color:#818cf8;'>/api/contact</a> &nbsp;|&nbsp; ");
        html.append("<a href='/api/meetings' style='color:#818cf8;'>/api/meetings</a>");
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }

    /** Simple HTML escape to prevent XSS */
    private String escapeHtml(String input) {
        if (input == null) return "—";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
