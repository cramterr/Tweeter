package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.TaskHandlers;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public interface StatusObserver extends ServiceObserver {
        default public void showToast() {}
    }

    private List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                int index = findUrlEndIndex(word);
                word = word.substring(0, index);
                containedUrls.add(word);
            }
        }
        return containedUrls;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);
                containedMentions.add(word);
            }
        }
        return containedMentions;
    }

    public void newStatus(String post, StatusObserver observer) {
        observer.showToast();
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
            PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                    newStatus, new PostStatusTaskHandler(observer));
            BackgroundTaskUtils.runTask(statusTask);
        } catch (Exception ex) {
            observer.logException(ex);
            observer.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public void loadMoreItems(String type, AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StatusObserver observer, String errorMessage, String exceptionMessage) {
        if (type == "Story") {
            GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken,
                    user, pageSize, lastStatus, new GetStoryTaskHandler(observer, errorMessage, exceptionMessage));
            BackgroundTaskUtils.runTask(getStoryTask);
        } else if (type == "Feed") {
            GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                    user, pageSize, lastStatus, new GetFeedTaskHandler(observer, errorMessage, exceptionMessage));
            BackgroundTaskUtils.runTask(getFeedTask);
        }
    }

}
