package cz.cilf.rewrite;

import javax.ejb.Stateless;

@Stateless
public class RewriteService {

    public boolean isPageFound(String url) {
        return !url.contains("second-page");
    }
}
