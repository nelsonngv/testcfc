package com.pbasolutions.android.json;

/**
 * Created by pbadell on 10/30/15.
 */
public class PBSNotesJSON extends PBSJson{
    PBSNoteJSON[] Notes;

    public PBSNoteJSON[] getNotes() {
        return Notes;
    }

    public void setNotes(PBSNoteJSON[] notes) {
        this.Notes = notes;
    }
}
