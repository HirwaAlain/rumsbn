package rw.rura.rums.module.complaints.service;

import org.springframework.stereotype.Component;
import rw.rura.rums.enums.ComplaintCategory;
import rw.rura.rums.enums.ComplaintSeverity;

import java.util.List;

/**
 * Rule-based classifier that infers complaint severity from category and description.
 * Category provides a baseline score; description keywords escalate or diminish it.
 *
 * Score → Severity mapping:
 *   1–2  → LOW
 *   3–4  → MEDIUM
 *   5–7  → HIGH
 *   8+   → CRITICAL
 */
@Component
public class SeverityClassifier {

    private static final List<String> CRITICAL_KEYWORDS = List.of(
            "fraud", "criminal", "illegal", "bribe", "extortion", "blackmail",
            "life-threatening", "life threatening", "emergency", "death",
            "identity theft", "embezzlement", "corruption"
    );

    private static final List<String> HIGH_KEYWORDS = List.of(
            "weeks", "months", "lawsuit", "legal action", "court", "data breach",
            "leaked", "personal data", "exposed", "no service", "disconnected",
            "repeated", "multiple times", "systematic", "multiple customers",
            "serious", "significant loss", "large amount", "total outage"
    );

    private static final List<String> MEDIUM_KEYWORDS = List.of(
            "days", "overcharged", "denied", "refused", "ignored", "interrupted",
            "several", "ongoing", "unresolved", "no response", "double charged"
    );

    private static final List<String> LOW_KEYWORDS = List.of(
            "once", "brief", "temporary", "minor", "small issue", "slight", "occasionally"
    );

    public ComplaintSeverity classify(ComplaintCategory category, String description) {
        int score = categoryBaseScore(category) + descriptionScore(description.toLowerCase());
        if (score >= 8) return ComplaintSeverity.CRITICAL;
        if (score >= 5) return ComplaintSeverity.HIGH;
        if (score >= 3) return ComplaintSeverity.MEDIUM;
        return ComplaintSeverity.LOW;
    }

    private int categoryBaseScore(ComplaintCategory category) {
        return switch (category) {
            case DATA_PRIVACY_BREACH      -> 7;
            case UNAUTHORIZED_CHARGES     -> 5;
            case TARIFF_OVERCHARGE        -> 4;
            case CONTRACT_VIOLATION       -> 4;
            case SERVICE_INTERRUPTION     -> 4;
            case BILLING_DISPUTE          -> 3;
            case POOR_QUALITY_OF_SERVICE  -> 2;
            case CONNECTION_DELAY         -> 2;
            case CUSTOMER_SERVICE_FAILURE -> 2;
            case OTHER                    -> 2;
        };
    }

    private int descriptionScore(String text) {
        // A critical keyword forces CRITICAL regardless of category
        for (String kw : CRITICAL_KEYWORDS) {
            if (text.contains(kw)) return 10;
        }

        int score = 0;

        int highMatches = 0;
        for (String kw : HIGH_KEYWORDS) {
            if (text.contains(kw) && ++highMatches <= 3) score++;
        }

        int medMatches = 0;
        for (String kw : MEDIUM_KEYWORDS) {
            if (text.contains(kw) && ++medMatches <= 2) score++;
        }

        for (String kw : LOW_KEYWORDS) {
            if (text.contains(kw)) score--;
        }

        return score;
    }
}
