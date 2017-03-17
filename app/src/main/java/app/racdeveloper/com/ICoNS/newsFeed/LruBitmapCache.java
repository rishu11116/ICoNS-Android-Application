package app.racdeveloper.com.ICoNS.newsFeed;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Rachit on 10/7/2016.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
    public static int getDefaultLruCacheSize(){
        final int maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize= maxMemory/8;

        return cacheSize;
    }

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String s) {
        return get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        put(s, bitmap);
    }
}
