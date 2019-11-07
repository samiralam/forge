package forge.quest.io;

import forge.ImageKeys;
import forge.deck.io.DeckSerializer;
import forge.deck.io.DeckStorage;
import forge.properties.ForgeConstants;
import forge.quest.QuestEvent;
import forge.quest.QuestEventDifficulty;
import forge.quest.QuestEventDuel;
import forge.util.FileSection;
import forge.util.FileUtil;
import forge.util.TextUtil;
import forge.util.storage.StorageReaderFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class QuestDuelReader extends StorageReaderFolder<QuestEventDuel> {
    
    private static final String WILD_DEFAULT_ICON_NAME = "Wild.jpg";
    private static final String WILD_DIR_NAME = "wild";
    
    public QuestDuelReader(File deckDir0) {
        super(deckDir0, QuestEvent.FN_GET_NAME);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public Map<String, QuestEventDuel> readAll() {
        
        List<File> dirList = new ArrayList<File>();
        dirList.add(directory);
        
        File directoryWild = new File(directory.getAbsolutePath() + ForgeConstants.PATH_SEPARATOR + WILD_DIR_NAME + ForgeConstants.PATH_SEPARATOR);
        if(directoryWild.exists()) {
            dirList.add(directoryWild);                            
        }
 
        final Map<String, QuestEventDuel> result = new TreeMap<>();
        
        for(File currDir : dirList) {
            final File[] files = currDir.listFiles(this.getFileFilter());
            for (final File file : files) {
                try {
                    final QuestEventDuel newDeck = this.read(file);
                    if (null == newDeck) {
                        final String msg = "An object stored in " + file.getPath() + " failed to load.\nPlease submit this as a bug with the mentioned file/directory attached.";
                        throw new RuntimeException(msg);
                    }

                    String newKey = keySelector.apply(newDeck);
                    if (result.containsKey(newKey)) {
                        System.err.println("StorageReaderFolder: an object with key " + newKey + " is already present - skipping new entry");
                    } else {
                        result.put(newKey, newDeck);                       
                    }
                } catch (final NoSuchElementException ex) {
                    final String message = TextUtil.concatWithSpace( file.getName(),"failed to load because ----", ex.getMessage());
                    objectsThatFailedToLoad.add(message);
                }
            }            
        }

        return result;
    }

    @Override
    protected QuestEventDuel read(File file) {
        final Map<String, List<String>> contents = FileSection.parseSections(FileUtil.readFile(file));
        final QuestEventDuel qc = new QuestEventDuel();

        // Common properties
        FileSection sectionMeta = FileSection.parse(contents.get("metadata"), "=");
        qc.setName(sectionMeta.get("Name")); // Challenges have unique titles
        
        boolean difficultySpecified = !StringUtils.isEmpty(sectionMeta.get("Difficulty"));
        if(difficultySpecified) {
            qc.setTitle(sectionMeta.get("Title"));
            qc.setDifficulty(QuestEventDifficulty.fromString(sectionMeta.get("Difficulty")));
            qc.setDescription(sectionMeta.get("Description", "").replace("\\n", "\n"));
            qc.setCardReward(sectionMeta.get("Card Reward"));
            qc.setIconImageKey(ImageKeys.ICON_PREFIX + sectionMeta.get("Icon"));
            if (sectionMeta.contains("Profile")) {
                qc.setProfile(sectionMeta.get("Profile"));
            }            
        } else {
            qc.setDifficulty(QuestEventDifficulty.WILD);
            qc.setTitle(sectionMeta.get("Title") != null ? sectionMeta.get("Title") : qc.getName());
            qc.setDescription(sectionMeta.get("Description") != null ? sectionMeta.get("Description") : "Wild opponent");
            qc.setIconImageKey(ImageKeys.ICON_PREFIX + (sectionMeta.get("Icon") != null ? sectionMeta.get("Icon") : WILD_DEFAULT_ICON_NAME));           
        }

        // Deck
        qc.setEventDeck(DeckSerializer.fromSections(contents));
        return qc;
    }

    @Override
    protected FilenameFilter getFileFilter() { 
        return DeckStorage.DCK_FILE_FILTER;
    }
}