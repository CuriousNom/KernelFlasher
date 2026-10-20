#!/system/bin/sh

## setup for testing:
unzip -p $Z tools*/busybox > $F/busybox 2>/dev/null;
unzip -p $Z META-INF/com/google/android/update-binary > $F/update-binary 2>/dev/null;
##

if [ ! -s $F/busybox ]; then
    BB=$(which busybox 2>/dev/null || echo "/system/bin/toybox")
    cp "$BB" $F/busybox 2>/dev/null || ln -sf "$BB" $F/busybox
fi

chmod 755 $F/busybox;
$F/busybox chmod 755 $F/update-binary 2>/dev/null || chmod 755 $F/update-binary;
$F/busybox chown root:root $F/busybox $F/update-binary 2>/dev/null || chown root:root $F/busybox $F/update-binary 2>/dev/null;

TMP=$F/tmp;

$F/busybox umount $TMP 2>/dev/null || umount $TMP 2>/dev/null;
$F/busybox rm -rf $TMP 2>/dev/null || rm -rf $TMP 2>/dev/null;
$F/busybox mkdir -p $TMP 2>/dev/null || mkdir -p $TMP;

$F/busybox mount -t tmpfs -o noatime tmpfs $TMP 2>/dev/null || mount -t tmpfs -o noatime tmpfs $TMP;
mount | grep -q " $TMP " || exit 1;

SH_CMD="$F/busybox ash"
$F/busybox ash -c 'exit 0' 2>/dev/null || SH_CMD="$F/busybox sh"

# update-binary <RECOVERY_API_VERSION> <OUTFD> <ZIPFILE>
AKHOME=$TMP/anykernel $SH_CMD $F/update-binary 3 1 "$Z";
RC=$?;

$F/busybox umount $TMP;
$F/busybox rm -rf $TMP;
$F/busybox mount -o ro,remount -t auto /;
$F/busybox rm -f $F/update-binary $F/busybox;

# work around libsu not cleanly accepting return or exit as last line
safereturn() { return $RC; }
safereturn;
