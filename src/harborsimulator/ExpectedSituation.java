/** Expected situation class is used as a data structure to store:
 *  instance name + superclasses (including restrictions)
 */
package harborsimulator;

import java.util.ArrayList;

public class ExpectedSituation {
    public String uri;
    public ArrayList<String> superClasses;
    
    
    public ExpectedSituation(String uri) {
        this.uri = uri;
        superClasses = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(); 
        s.append(uri.substring(uri.indexOf('#') + 1, uri.length() - 1));
        s.append(" >> ");
        for(String sc : superClasses) {
            s.append(sc.substring(sc.indexOf('#') + 1, sc.length() - 1) + " ");        
        }
        return s.toString();
    }
            
}
