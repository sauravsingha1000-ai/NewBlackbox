package android.database;
interface IContentObserver {
    oneway void onChange(boolean selfChange, in Uri uri, int userId);
}
