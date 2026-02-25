package de.mueller_constantin.attoly.api.domain.result;

import de.mueller_constantin.attoly.api.repository.model.Complaint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ComplaintResult {
    private UUID id;
    private Complaint.Reason reason;
    private String comment;
    private String shortcutTag;
}
