package rw.rura.rums.module.complaints.dto;

import rw.rura.rums.module.complaints.repository.ComplaintsBySectorProjection;

public record ComplaintsBySectorPoint(String sector, long count) {

    public static ComplaintsBySectorPoint fromProjection(ComplaintsBySectorProjection p) {
        return new ComplaintsBySectorPoint(p.getSector(), p.getCount() != null ? p.getCount() : 0L);
    }
}
